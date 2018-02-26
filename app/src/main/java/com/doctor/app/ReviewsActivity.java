package com.doctor.app;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.RatingBar;
import android.widget.TextView;

import com.doctor.app.adapter.FeedbackAdapter;
import com.doctor.app.helper.AppConst;
import com.doctor.app.model.DoctorDetailPojo;

public class ReviewsActivity extends AppCompatActivity {

    TextView tvtotrating;
    RatingBar rboverall;
    RecyclerView rv;

    FeedbackAdapter fadapter;
    Context context;
    String TAG="ReviewsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context=ReviewsActivity.this;

        tvtotrating=(TextView)findViewById(R.id.tvtotrating);
        rboverall=(RatingBar)findViewById(R.id.rbtotal);
        rv=(RecyclerView)findViewById(R.id.rvfeedback);
        rv.setLayoutManager(new LinearLayoutManager(context));
        rv.setHasFixedSize(true);

        getFeedbacks();
    }

    private void getFeedbacks() {
        DoctorDetailPojo pojo= AppConst.selDoctor;
        if(pojo.getOverall_rating()!=null) {
            Float rating = Float.parseFloat(pojo.getOverall_rating() + "");
            String srating = String.format("%.2f", rating);
            tvtotrating.setText(srating + "");
            rboverall.setRating(rating);
        }else{
            tvtotrating.setText(0.0 + "");
            rboverall.setRating((float)0);
        }
        if(pojo.getRatings().size()>0){
            fadapter=new FeedbackAdapter(pojo.getRatings(),context);
            rv.setAdapter(fadapter);
        }
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
