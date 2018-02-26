package com.doctor.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.doctor.app.R;
import com.doctor.app.model.PrescriptionPojo;
import com.github.curioustechizen.ago.RelativeTimeTextView;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class PrescriptionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 1;
    private List<PrescriptionPojo> list;
    Context context;
    String TAG="PrescriptionAdapter";
    SimpleDateFormat tmfmt=new SimpleDateFormat("dd MMM yyyy, HH:mm");
    SimpleDateFormat defaulttmfmt=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public PrescriptionAdapter(List<PrescriptionPojo> list, Context mcontext) {
        context = mcontext;
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.prescription_row, parent, false);
        return new ViewHolderPosts(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final PrescriptionPojo model=list.get(position);
        final ViewHolderPosts itemview = (ViewHolderPosts) holder;

        itemview.tvpre.setText(model.getPrescription());
        Date d=null;
        try {
            d=defaulttmfmt.parse(model.getDate_added()+" "+model.getTime_added());
//            itemview.tvtime.setText(tmfmt.format(d));
           // Date dt=defaulttmfmt.parse(end);
            itemview.tvtime.setReferenceTime(d.getTime());
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
        TextView tvpre;
        RelativeTimeTextView tvtime;
        public ViewHolderPosts(View itemView) {
            super(itemView);
          //  tvtime = (TextView) itemView.findViewById(R.id.tvtime);
            tvpre = (TextView) itemView.findViewById(R.id.tvpre);
            tvtime = (RelativeTimeTextView)itemView.findViewById(R.id.tvtime);
        }
    }
}
