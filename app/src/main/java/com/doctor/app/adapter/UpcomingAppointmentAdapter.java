package com.doctor.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doctor.app.AppointmentDetail;
import com.doctor.app.Doctor.Detail;
import com.doctor.app.R;
import com.doctor.app.helper.AppConst;
import com.doctor.app.model.AppointmentPojo;
import com.doctor.app.view.CropSquareTransformation;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class UpcomingAppointmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 1;
    private List<AppointmentPojo> list;
    Context context;
    String TAG="UpcomingAppointmentAdapter";
    SimpleDateFormat defaultfmt=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat dtfmt=new SimpleDateFormat("dd MMM yyyy, hh:mm a");

    public UpcomingAppointmentAdapter(List<AppointmentPojo> list, Context mcontext) {
        context = mcontext;
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_row, parent, false);
        return new ViewHolderPosts(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final AppointmentPojo model=list.get(position);
        final ViewHolderPosts itemview = (ViewHolderPosts) holder;

        itemview.tvname.setText(model.getName());
        String t=model.getAppointment_date()+" "+model.getAppointment_time();
        Date dt=null;
        try {
            dt=defaultfmt.parse(t);
           // itemview.tvtime.setText(dtfmt.format(dt));
            itemview.tvtime.setReferenceTime(dt.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String note=model.getNote();
        if(note!=null){
            if(note.trim().length()>0) {
                itemview.tvnote.setVisibility(View.VISIBLE);
                itemview.tvnote.setText(note);
            }else
                itemview.tvnote.setVisibility(View.GONE);
        }else{
            itemview.tvnote.setVisibility(View.GONE);
        }
        String pic=model.getPhoto();
        if(pic==null)
            pic="";
        if(pic.trim().length()>0){
            Picasso.with(context)
                    .load(AppConst.profile_img_url + pic)
                    .transform(new CropSquareTransformation())
                    .placeholder(R.drawable.ic_user)
                    .error(R.drawable.ic_user)
                    .into(itemview.iv);
        }

        String status=model.getAppointment_status();
        itemview.tvstatus.setVisibility(View.VISIBLE);
        itemview.tvstatus.setText(status);
        if(status.equals("pending")){
            itemview.tvstatus.setTextColor(context.getResources().getColor(android.R.color.holo_blue_bright));
        }else if(status.equals("accepted")){
            itemview.tvstatus.setTextColor(context.getResources().getColor(android.R.color.holo_green_light));
        }else{
            itemview.tvstatus.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        }

        itemview.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(context, Detail.class);
                AppConst.selAppointment=model;
                context.startActivity(it);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {

        return list.size();

    }

    public class ViewHolderPosts extends RecyclerView.ViewHolder {
        TextView tvname,tvnote,tvstatus;
        RelativeTimeTextView tvtime;
        ImageView iv;
        LinearLayout ll;

        public ViewHolderPosts(View itemView) {
            super(itemView);
            tvname = (TextView) itemView.findViewById(R.id.tvdrname);
            tvtime = (RelativeTimeTextView) itemView.findViewById(R.id.tvtime);
            tvnote = (TextView) itemView.findViewById(R.id.tvnote);
            tvstatus = (TextView) itemView.findViewById(R.id.tvstatus);
            iv = (ImageView) itemView.findViewById(R.id.ivdoctor);
            ll=(LinearLayout)itemView.findViewById(R.id.llappointment);
        }

    }

}
