package com.doctor.app.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.doctor.app.R;
import com.doctor.app.model.Rating;
import com.github.curioustechizen.ago.RelativeTimeTextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 1;
    private List<Rating> list;
    Context context;
    String TAG="FeedbackAdapter";
    SimpleDateFormat dtfmt=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat fmt=new SimpleDateFormat("dd/MM/yyyy");

    public FeedbackAdapter(List<Rating> list, Context mcontext) {
        context = mcontext;
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reviews_row, parent, false);
        return new ViewHolderPosts(view);

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final Rating model=list.get(position);
        final ViewHolderPosts itemview = (ViewHolderPosts) holder;
        float rating= 0;
        if(model.getRatings()!=null)
            rating=Float.parseFloat(model.getRatings()+"");
        else
            rating=0;

        itemview.tvcmt.setText(model.getReview_text());
        itemview.tvby.setText(model.getPatient_name());

        if(rating>0){
            itemview.rb.setRating(rating);
        }else{
            itemview.rb.setRating((float)0);
        }

        Date dt=null;
        try {
            dt=dtfmt.parse(model.getDate_time());
//            itemview.tvdate.setText(fmt.format(dt));
            itemview.tvdate.setReferenceTime(dt.getTime());
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
        TextView tvcmt,tvby;
        RelativeTimeTextView tvdate;
        RatingBar rb;

        public ViewHolderPosts(View itemView) {
            super(itemView);
            tvcmt = (TextView) itemView.findViewById(R.id.tvcmt);
            tvby=(TextView)itemView.findViewById(R.id.tvby);
            tvdate=(RelativeTimeTextView) itemView.findViewById(R.id.tvdate);
            rb=(RatingBar)itemView.findViewById(R.id.rbfeedback);
        }
    }

}
