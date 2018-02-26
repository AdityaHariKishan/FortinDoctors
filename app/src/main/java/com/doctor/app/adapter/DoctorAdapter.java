package com.doctor.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.doctor.app.DoctorDetail;
import com.doctor.app.PrescriptionDetail;
import com.doctor.app.R;
import com.doctor.app.helper.AppConst;
import com.doctor.app.model.DepartmentPojo;
import com.doctor.app.model.DoctorPojo;
import com.doctor.app.view.CropSquareTransformation;
import com.squareup.picasso.Picasso;

import java.util.List;


public class DoctorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 1;
    private List<DoctorPojo> list;
    Context context;
    String TAG="DoctorAdapter",screen;

    public DoctorAdapter(List<DoctorPojo> list, Context mcontext,String screen) {
        context = mcontext;
        this.list = list;
        this.screen=screen;
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctor_row, parent, false);
        return new ViewHolderPosts(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final DoctorPojo model=list.get(position);
        final ViewHolderPosts itemview = (ViewHolderPosts) holder;

        itemview.tvname.setText(model.getDoctor_name());
        itemview.tvclinic.setText(model.getClinic_name());
        Picasso.with(context)
                .load(AppConst.doctor_img_url+model.getDoctor_image())
                .transform(new CropSquareTransformation())
                .placeholder(R.drawable.ic_user)
                .error(R.drawable.ic_user)
                .into(itemview.ivdoctor);

        itemview.lldoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(screen.equals("Prescription")){
                    Intent it=new Intent(context, PrescriptionDetail.class);
                    AppConst.seldr=model;
                    context.startActivity(it);
                }else {
                    Intent it = new Intent(context, DoctorDetail.class);
                    it.putExtra("doctorid", model.getId());
                    context.startActivity(it);
                }
            }
        });

        if(model.getDistance().trim().length()<=0){
            itemview.lldist.setVisibility(View.INVISIBLE);
        }else {
            itemview.lldist.setVisibility(View.VISIBLE);
            String dist = String.format("%.2f", Float.parseFloat(model.getDistance()));
            itemview.tvdistance.setText("Distance: " + dist + " km");
        }

        if(model.getOverall_rating()!=null) {
            Float rating=Float.parseFloat(model.getOverall_rating()+"");
            itemview.rb.setRating(rating);
        }else{
            itemview.rb.setRating(0);
        }

        if(model.getEducation_details().trim().length()<=0){
            itemview.lledu.setVisibility(View.GONE);
        }else{
            itemview.lledu.setVisibility(View.VISIBLE);
            itemview.tvedu.setText(model.getEducation_details());
        }

        if(model.getDepartment()==null){
            itemview.lldep.setVisibility(View.GONE);
        }else if(model.getDepartment().size()>0){
            itemview.lldep.setVisibility(View.VISIBLE);
            String dp="";
            for(DepartmentPojo d:model.getDepartment()){
                if(dp.length()<=0)
                    dp=d.getName();
                else
                    dp=dp+","+d.getName();
            }
            itemview.tvdep.setText(dp);
        }else{
            itemview.lldep.setVisibility(View.GONE);
        }

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
        TextView tvname,tvclinic,tvdistance,tvedu,tvdep;
        ImageView ivdoctor;
        LinearLayout lldoctor,lledu,lldep,lldist;
        RatingBar rb;

        public ViewHolderPosts(View itemView) {
            super(itemView);
            tvname = (TextView) itemView.findViewById(R.id.tvname);
            tvclinic = (TextView) itemView.findViewById(R.id.tvclinic);
            ivdoctor=(ImageView)itemView.findViewById(R.id.ivdoctor);
            lldoctor=(LinearLayout)itemView.findViewById(R.id.lldoctor);
            lledu=(LinearLayout)itemView.findViewById(R.id.lledu);
            lldep=(LinearLayout)itemView.findViewById(R.id.lldept);
            lldist=(LinearLayout)itemView.findViewById(R.id.lldist);
            tvdistance=(TextView)itemView.findViewById(R.id.tvdistance);
            tvdep=(TextView)itemView.findViewById(R.id.tvdepartment);
            tvedu=(TextView)itemView.findViewById(R.id.tveducation);
            rb=(RatingBar)itemView.findViewById(R.id.rb);
        }

    }

}
