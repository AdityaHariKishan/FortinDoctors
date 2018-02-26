package com.doctor.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doctor.app.R;
import com.doctor.app.WriteReview;
import com.doctor.app.helper.AppConst;
import com.doctor.app.model.M;
import com.doctor.app.model.NotificationPojo;
import com.github.curioustechizen.ago.RelativeTimeTextView;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 1;
    private List<NotificationPojo> list;
    Context context;
    String TAG="NotificationAdapter";
    SimpleDateFormat defaulttmfmt=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat tmfmt=new SimpleDateFormat("dd/MM/yyyy hh:mm a");//new SimpleDateFormat("dd MMM yyyy, hh:mm a");//

    public NotificationAdapter(List<NotificationPojo> list, Context mcontext) {
        context = mcontext;
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_row, parent, false);
        return new ViewHolderPosts(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final NotificationPojo model=list.get(position);
        final ViewHolderPosts itemview = (ViewHolderPosts) holder;

        itemview.tv.setText(model.getMessage());
        Date d=null;
        try {
            d=defaulttmfmt.parse(model.getTime());
//            itemview.tvtime.setText(tmfmt.format(d));
            itemview.tvtime.setReferenceTime(d.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        itemview.llnotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!M.getRole(context).equals("doctor")){
                    if(model.getAppointment_status().equals(AppConst.status_complete)) {
                        Intent it = new Intent(context, WriteReview.class);
                        AppConst.selNotification = model;
                        context.startActivity(it);
                    }
                }
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
        TextView tv;
        RelativeTimeTextView tvtime;
        LinearLayout llnotify;

        public ViewHolderPosts(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.tvnotify);
            tvtime = (RelativeTimeTextView) itemView.findViewById(R.id.tvtime);
            llnotify=(LinearLayout)itemView.findViewById(R.id.llnotify);
        }

    }

}
