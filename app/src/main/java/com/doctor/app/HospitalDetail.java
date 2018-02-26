package com.doctor.app;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.doctor.app.adapter.ViewPagerAdapter;
import com.doctor.app.helper.AppConst;
import com.doctor.app.model.HospitalPojo;
import com.doctor.app.view.CropSquareTransformation;
import com.squareup.picasso.Picasso;

public class HospitalDetail extends AppCompatActivity {

    TextView tvclinic,tvdistance,tvaddress,tvphone,tvabout,tvdrname,tvdrphone;
    RatingBar rb;
    FrameLayout llpager;
    ViewPager pager_feed;
    LinearLayout pager_indicator;
    ImageView ivdoctor;
    String TAG="HospitalDetail";
    Context context;
    HospitalPojo model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context=HospitalDetail.this;

        if(AppConst.selHospital==null)
            finish();
        else
            model=AppConst.selHospital;
        initview();

    }

    private void initview() {
        tvclinic = (TextView) findViewById(R.id.tvclinic);
        llpager=(FrameLayout) findViewById(R.id.llpager);
        pager_feed=(ViewPager)findViewById(R.id.pager_feed);
        pager_indicator = (LinearLayout)findViewById(R.id.viewPagerCountDots);
        tvdistance=(TextView)findViewById(R.id.tvdistance);
        rb=(RatingBar)findViewById(R.id.rb);
        tvaddress=(TextView)findViewById(R.id.tvaddress);
        tvphone=(TextView)findViewById(R.id.tvphone);
        tvabout=(TextView)findViewById(R.id.tvaboutus);
        tvdrname=(TextView)findViewById(R.id.tvdrname);
        tvdrphone=(TextView)findViewById(R.id.tvdrphone);
        ivdoctor=(ImageView)findViewById(R.id.ivdoctor);
        setData();
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

    private void setData() {
        getSupportActionBar().setTitle(model.getClinic_name());
        tvclinic.setText(model.getClinic_name());
        String dist=String.format("%.2f",Float.parseFloat(model.getDistance()));
        tvdistance.setText("Distance: "+dist+" km");
        Log.d(TAG,model.getClinic_name()+" "+model.getOverall_rating());
        if(model.getOverall_rating()!=null) {
            Float rating=Float.parseFloat(model.getOverall_rating()+"");
            rb.setRating(rating);
        }else{
            rb.setRating(0);
        }

        if(model.getClinic_images().size()>0){
            llpager.setVisibility(View.VISIBLE);
            ViewPagerAdapter mAdapter = new ViewPagerAdapter(context, model.getClinic_images());
            pager_feed.setAdapter(mAdapter);
            pager_feed.setCurrentItem(0);
            final int dotsCount=mAdapter.getCount();
            ImageView[] dots=null;
            if(model.getClinic_images().size()>1) {
                pager_indicator.setVisibility(View.VISIBLE);
                dots=setUiPageViewController( pager_indicator,dotsCount);
            }else
                pager_indicator.setVisibility(View.INVISIBLE);

            final ImageView[] finalDots = dots;
            pager_feed.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
            llpager.setVisibility(View.GONE);
        }

        tvaddress.setText(model.getAddress());
        tvphone.setText(model.getClinic_phone());
        tvabout.setText(model.getAbout());
        tvdrname.setText(model.getDoctor_name());
        tvdrphone.setText(model.getPersonal_phone());
        Picasso.with(context)
                .load(AppConst.doctor_img_url+model.getDoctor_image())
                .transform(new CropSquareTransformation())
                .placeholder(R.drawable.ic_user)
                .error(R.drawable.ic_user)
                .into(ivdoctor);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
