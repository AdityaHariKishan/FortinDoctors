package com.doctor.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.util.Log;

import com.doctor.app.HospitalDetail;
import com.doctor.app.R;
import com.doctor.app.helper.AppConst;
import com.doctor.app.model.HospitalPojo;

import java.util.List;


public class HospitalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 1;
    private List<HospitalPojo> list;
    Context context;
    String TAG="HospitalAdapter";

    public HospitalAdapter(List<HospitalPojo> list, Context mcontext) {
        context = mcontext;
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hospital_row, parent, false);
        return new ViewHolderPosts(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final HospitalPojo model=list.get(position);
        final ViewHolderPosts itemview = (ViewHolderPosts) holder;

        itemview.tvclinic.setText(model.getClinic_name());
        String dist=String.format("%.2f",Float.parseFloat(model.getDistance()));
        itemview.tvdistance.setText("Distance: "+dist+" km");
        Log.d(TAG,model.getClinic_name()+" "+model.getOverall_rating());
        if(model.getOverall_rating()!=null) {
            Float rating=Float.parseFloat(model.getOverall_rating()+"");
            itemview.rb.setRating(rating);
        }else{
            itemview.rb.setRating(0);
        }

        if(model.getClinic_images().size()>0){
            itemview.llpager.setVisibility(View.VISIBLE);
            ViewPagerAdapter mAdapter = new ViewPagerAdapter(context, model.getClinic_images());
            itemview.pager_feed.setAdapter(mAdapter);
            itemview.pager_feed.setCurrentItem(0);
            final int dotsCount=mAdapter.getCount();
            ImageView[] dots=null;
            if(model.getClinic_images().size()>1) {
                itemview.pager_indicator.setVisibility(View.VISIBLE);
                dots=setUiPageViewController( itemview.pager_indicator,dotsCount);
            }else
                itemview.pager_indicator.setVisibility(View.INVISIBLE);

            final ImageView[] finalDots = dots;
            itemview.pager_feed.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int pos, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int pos) {
                    if(finalDots!=null) {
                        for (int i = 0; i < dotsCount; i++) {
                            finalDots[i].setImageDrawable(context.getResources().getDrawable(R.drawable.nonselecteditem_dot));
                        }
                        finalDots[pos].setImageDrawable(context.getResources().getDrawable(R.drawable.selecteditem_dot));
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }else{
            itemview.llpager.setVisibility(View.GONE);
        }

        itemview.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(context, HospitalDetail.class);
                AppConst.selHospital=model;
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
        TextView tvclinic,tvdistance;
        RatingBar rb;
        FrameLayout llpager;
        ViewPager pager_feed;
        LinearLayout pager_indicator,ll;

        public ViewHolderPosts(View itemView) {
            super(itemView);
            tvclinic = (TextView) itemView.findViewById(R.id.tvclinic);
            llpager=(FrameLayout)itemView. findViewById(R.id.llpager);
            pager_feed=(ViewPager)itemView.findViewById(R.id.pager_feed);
            pager_indicator = (LinearLayout)itemView.findViewById(R.id.viewPagerCountDots);
            tvdistance=(TextView)itemView.findViewById(R.id.tvdistance);
            rb=(RatingBar)itemView.findViewById(R.id.rb);
            ll=(LinearLayout)itemView.findViewById(R.id.llhospital);
        }

    }

    private ImageView[] setUiPageViewController(LinearLayout pager_indicator,int dotsCount) {

        ImageView[] dots = new ImageView[dotsCount];
        pager_indicator.removeAllViews();
        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(context);
            dots[i].setImageDrawable(context.getResources().getDrawable(R.drawable.nonselecteditem_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);
            pager_indicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(context.getResources().getDrawable(R.drawable.selecteditem_dot));

        return dots;
    }

}
