package com.doctor.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doctor.app.DoctorDetail;
import com.doctor.app.R;
import com.doctor.app.model.Duty_timing;
import com.doctor.app.model.Duty_timing;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class TimingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 1;
    private List<Duty_timing> list;
    Context context;
    String TAG="TimingAdapter";
    SimpleDateFormat tmfmt=new SimpleDateFormat("HH:mm");
    SimpleDateFormat defaulttmfmt=new SimpleDateFormat("HH:mm:ss");

    public TimingAdapter(List<Duty_timing> list, Context mcontext) {
        context = mcontext;
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.timing_row, parent, false);
        return new ViewHolderPosts(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final Duty_timing model=list.get(position);
        final ViewHolderPosts itemview = (ViewHolderPosts) holder;

        itemview.tvday.setText(model.getDays());
        Date d=null,d1=null;
        try {
            d=defaulttmfmt.parse(model.getDuty_start_time());
            d1=defaulttmfmt.parse(model.getDuty_end_time());
            itemview.tvstart.setText(tmfmt.format(d));
            itemview.tvend.setText(tmfmt.format(d1));
        } catch (ParseException e) {
            e.printStackTrace();
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
        TextView tvday,tvstart,tvend;

        public ViewHolderPosts(View itemView) {
            super(itemView);
            tvday = (TextView) itemView.findViewById(R.id.tvday);
            tvstart = (TextView) itemView.findViewById(R.id.tvstart);
            tvend = (TextView) itemView.findViewById(R.id.tvend);
        }

    }

}
