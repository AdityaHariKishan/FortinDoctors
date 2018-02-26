package com.doctor.app;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.doctor.app.adapter.TimingAdapter;
import com.doctor.app.adapter.ViewPagerAdapter;
import com.doctor.app.helper.AppConst;
import com.doctor.app.model.Clinic_image;
import com.doctor.app.model.DepartmentPojo;
import com.doctor.app.model.DoctorDetailPojo;
import com.doctor.app.model.M;
import com.doctor.app.view.CropSquareTransformation;
import com.doctor.app.webservice.APIAuthentication;
import com.doctor.app.webservice.Service;
import com.squareup.picasso.Picasso;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class DoctorDetail extends AppCompatActivity implements View.OnClickListener {

    ImageView ivdr;
    TextView tvclinic,tvdrname,tvaddress,tvcontact,tvdrphone,tvabout,tvedu,tvdep,tvdistance,tvreview;
    RecyclerView rvtimimg;
    Button btn;
    RatingBar rb;
    LinearLayout lldphone,llcphone,llabt,pager_indicator,lledu,lldept,lldist,lldep;
    FrameLayout llpager;
    ViewPager pager_feed;
    Context context;
    String TAG="DoctorDetail",doctorid="";
    DoctorDetailPojo pojo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context=DoctorDetail.this;
        if(getIntent().getExtras()!=null){
            doctorid=getIntent().getStringExtra("doctorid");
        }else{

        }

        initview();
    }

    private void initview() {

        llcphone=(LinearLayout)findViewById(R.id.llcphone);
        lldphone=(LinearLayout)findViewById(R.id.lldphone);
        llabt=(LinearLayout)findViewById(R.id.llabt);
        tvdrname=(TextView)findViewById(R.id.tvdoctorname);
        tvclinic=(TextView)findViewById(R.id.tvclinic);
        tvaddress=(TextView)findViewById(R.id.tvaddress);
        tvcontact=(TextView)findViewById(R.id.tvcontact);
        tvdrphone=(TextView)findViewById(R.id.tvdrphone);
        tvabout=(TextView)findViewById(R.id.tvabout);
        tvedu=(TextView)findViewById(R.id.tveducation);
        tvdep=(TextView)findViewById(R.id.tvdepartment);
        tvdistance=(TextView)findViewById(R.id.tvdistance);
        tvreview=(TextView)findViewById(R.id.tvreview);
        rvtimimg=(RecyclerView)findViewById(R.id.rvtiming);
        llpager=(FrameLayout) findViewById(R.id.llpager);
        pager_feed=(ViewPager)findViewById(R.id.pager_feed);
        pager_indicator = (LinearLayout)findViewById(R.id.viewPagerCountDots);
        ivdr=(ImageView)findViewById(R.id.ivdr);
        rvtimimg.setLayoutManager(new LinearLayoutManager(context));
        rvtimimg.setHasFixedSize(true);
        rvtimimg.setNestedScrollingEnabled(false);
        btn=(Button)findViewById(R.id.btnappointment);
        lledu=(LinearLayout)findViewById(R.id.lledu);
        lldept=(LinearLayout)findViewById(R.id.lldept);
        lldist=(LinearLayout)findViewById(R.id.lldist);
        lldep=(LinearLayout)findViewById(R.id.lldept);
        rb=(RatingBar)findViewById(R.id.rb);
        getDoctorDetail();

        btn.setOnClickListener(this);
        tvreview.setOnClickListener(this);
    }


    private void getDoctorDetail() {

        M.showLoadingDialog(context);
        APIAuthentication mAuthenticationAPI = Service.createService(context,APIAuthentication.class);
        Call<DoctorDetailPojo> call = mAuthenticationAPI.getDoctorDetail(doctorid);
        call.enqueue(new retrofit2.Callback<DoctorDetailPojo>() {
            @Override
            public void onResponse(Call<DoctorDetailPojo> call, Response<DoctorDetailPojo> response) {
                //Log.d("response:","data:"+response);
                if (response.isSuccessful()) {
                    pojo = response.body();
                    String clinicnm="",doctornm="",address="",contact="",drphone="",abt="",drimg="";
                    if(pojo!=null){
                        clinicnm=pojo.getClinic_name();
                        doctornm=pojo.getDoctor_name();
                        address=pojo.getAddress();
                        contact=pojo.getClinic_phone();
                        drphone=pojo.getPersonal_phone();
                        abt=pojo.getAbout();
                        drimg=pojo.getDoctor_image();
                        tvclinic.setText(clinicnm);
                        tvdrname.setText(doctornm);
                        Picasso.with(context)
                                .load(AppConst.doctor_img_url+drimg)
                                .transform(new CropSquareTransformation())
                                .placeholder(R.drawable.ic_user)
                                .error(R.drawable.ic_user)
                                .into(ivdr);
                        tvaddress.setText(address);
                        if(drphone.trim().length()<=0){
                            lldphone.setVisibility(View.GONE);
                        }else {
                            lldphone.setVisibility(View.VISIBLE);
                            tvdrphone.setText(drphone);
                        }
                        if(contact.trim().length()<=0){
                            llcphone.setVisibility(View.GONE);
                        }else {
                            llcphone.setVisibility(View.VISIBLE);
                            tvcontact.setText(contact);
                        }
                        if(abt.trim().length()<=0){
                            llabt.setVisibility(View.GONE);
                        }else{
                            llabt.setVisibility(View.VISIBLE);
                            tvabout.setText(abt);
                        }
                        if(pojo.getDuty_timing().size()>0){
                            btn.setVisibility(View.VISIBLE);
                            TimingAdapter tadapter=new TimingAdapter(pojo.getDuty_timing(),context);
                            rvtimimg.setAdapter(tadapter);
                        }else{
                            btn.setVisibility(View.GONE);
                        }
                        if(pojo.getClinic_images().size()>0){
                            llpager.setVisibility(View.VISIBLE);
                            ViewPagerAdapter mAdapter = new ViewPagerAdapter(context, pojo.getClinic_images());
                            pager_feed.setAdapter(mAdapter);
                            pager_feed.setCurrentItem(0);
                            final int dotsCount=mAdapter.getCount();
                            ImageView[] dots=null;
                            if(pojo.getClinic_images().size()>1) {
                                pager_indicator.setVisibility(View.VISIBLE);
                                dots=setUiPageViewController(pager_indicator,dotsCount);
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

                        if(pojo.getDistance().trim().length()<=0){
                            lldist.setVisibility(View.INVISIBLE);
                        }else {
                            lldist.setVisibility(View.VISIBLE);
                            String dist = String.format("%.2f", Float.parseFloat(pojo.getDistance()));
                            tvdistance.setText("Distance: " + dist + " km");
                        }

                        if(pojo.getOverall_rating()!=null) {
                            Float rating=Float.parseFloat(pojo.getOverall_rating()+"");
                            rb.setRating(rating);
                        }else{
                            rb.setRating(0);
                        }

                        if(pojo.getEducation_details().trim().length()<=0){
                            lledu.setVisibility(View.GONE);
                        }else{
                            lledu.setVisibility(View.VISIBLE);
                            tvedu.setText(pojo.getEducation_details());
                        }

                        if(pojo.getDepartment()==null){
                            lldep.setVisibility(View.GONE);
                        }else if(pojo.getDepartment().size()>0){
                            lldep.setVisibility(View.VISIBLE);
                            String dp="";
                            for(DepartmentPojo d:pojo.getDepartment()){
                                if(dp.length()<=0)
                                    dp=d.getName();
                                else
                                    dp=dp+","+d.getName();
                            }
                            tvdep.setText(dp);
                        }else{
                            lldep.setVisibility(View.GONE);
                        }
                    }
                    M.hideLoadingDialog();
                } else {
                    int statusCode = response.code();
                    M.hideLoadingDialog();
                    // handle request errors yourself
                    ResponseBody errorBody = response.errorBody();
                }
            }

            @Override
            public void onFailure(Call<DoctorDetailPojo> call, Throwable t) {
                Log.d("response:","fail:"+t.getMessage());
                M.hideLoadingDialog();
                // handle execution failures like no internet connectivity
            }
        });
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

    @Override
    public void onClick(View v) {
        if(v==btn){
            Intent it=new Intent(context,BookAppointment.class);
            AppConst.selDoctor=pojo;
            startActivity(it);
        }else if(v==tvreview){
            Intent it=new Intent(context,ReviewsActivity.class);
            AppConst.selDoctor=pojo;
            startActivity(it);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

//    public class ViewPagerAdapter extends PagerAdapter {
//
//        private Context mContext;
//        private List<Clinic_image> mResources;
//
//        public ViewPagerAdapter(Context mContext,List<Clinic_image> list) {
//            this.mContext = mContext;
//            this.mResources = list;
//        }
//
//        @Override
//        public int getCount() {
//            return mResources.size();
//        }
//
//        @Override
//        public boolean isViewFromObject(View view, Object object) {
//            return view == ((LinearLayout) object);
//        }
//
//        @Override
//        public Object instantiateItem(ViewGroup container, int position) {
//            View itemView = LayoutInflater.from(context).inflate(R.layout.fullimage_row, container, false);
//
//            Clinic_image model = mResources.get(position);
//
//            ImageView tv=(ImageView) findViewById(R.id.fullimage);
//            tv.setScaleType(ImageView.ScaleType.FIT_XY);
//            // Log.d("photo","url:"+AppConst.MAIN+model);
//            Picasso.with(context)
//                    .load(AppConst.clinic_img_url+model.getImage_name())
//                    .placeholder(R.drawable.default_image)
//                    .error(R.drawable.default_image)
//                    .into(tv);
//            container.addView(itemView);
//
//            return itemView;
//        }
//
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            container.removeView((LinearLayout) object);
//        }
//    }


}
