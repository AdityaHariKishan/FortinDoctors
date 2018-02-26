package com.doctor.app;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.doctor.app.helper.AppConst;
import com.doctor.app.model.DoctorDetailPojo;
import com.doctor.app.model.M;
import com.doctor.app.model.NotificationPojo;
import com.doctor.app.model.SuccessPojo;
import com.doctor.app.webservice.APIAuthentication;
import com.doctor.app.webservice.Service;
import com.squareup.picasso.Picasso;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class WriteReview extends AppCompatActivity implements View.OnClickListener {

    TextView tvname,btnsend;
    RatingBar rb,rbprovider;
    ImageView iv;
    EditText etcmt;
    Context context;
    String TAG="WriteReview";
    String user_rating="0",doctor_id,userid;
    NotificationPojo req;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context=WriteReview.this;
        if(AppConst.selNotification==null)
            finish();
        else
            req=AppConst.selNotification;
        initview();

        userid=M.getID(context);
        doctor_id=req.getDoctor_id();
    }

    private void initview() {

        iv=(ImageView)findViewById(R.id.ivprofile);
        tvname=(TextView)findViewById(R.id.tvname);
        rb=(RatingBar)findViewById(R.id.rb);
        rbprovider=(RatingBar)findViewById(R.id.rbprovider);
        etcmt=(EditText)findViewById(R.id.etcmt);
        btnsend=(TextView)findViewById(R.id.btnsend);

        rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,boolean fromUser) {
                Toast.makeText(getApplicationContext(),"Your Selected Ratings  : " + String.valueOf(rating),Toast.LENGTH_SHORT).show();
                user_rating=String.valueOf(rating);
            }
        });

        btnsend.setOnClickListener(this);

        if(req!=null){
             tvname.setText(req.getDoctor_name());

                String pic = req.getDoctor_image();
                if (pic != null) {
                    Picasso.with(context)
                            .load(AppConst.doctor_img_url + pic)
                            .placeholder(R.drawable.ic_user)
                            .error(R.drawable.ic_user)
                            .into(iv);
                }

        }else{
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        if(v==btnsend){
            String cmt="";
            if(etcmt.getText().toString().trim().length()>0)
                cmt=etcmt.getText().toString();
            //Log.d(TAG,cmt+" "+user_rating+" "+req.getTo_id()+" "+req.getFrom_id());
            sendReview(cmt);
        }
    }

    public void sendReview(String comment){
        String from=userid,to=doctor_id;

        //Log.d(TAG,to+" "+from+" "+user_rating+" "+comment);
        M.showLoadingDialog(context);
        APIAuthentication mAuthenticationAPI = Service.createService(context,APIAuthentication.class);
        Call<SuccessPojo> call = mAuthenticationAPI.addReview(to,from,user_rating,comment);
        call.enqueue(new retrofit2.Callback<SuccessPojo>() {
            @Override
            public void onResponse(Call<SuccessPojo> call, Response<SuccessPojo> response) {
                Log.d("response:","data:"+response);
                M.hideLoadingDialog();
                if (response.isSuccessful()) {
                    SuccessPojo pojo=response.body();
                    if(pojo!=null){
                        if(pojo.getSuccess().equals("1")) {
                            Toast.makeText(context,"Review send successfully...",Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }else if(pojo.getSuccess().equals("0")){
                            Toast.makeText(context,"Review not sent",Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {
                    int statusCode = response.code();

                    // handle request errors yourself
                    ResponseBody errorBody = response.errorBody();
                    Log.d(TAG,"error:"+statusCode+" "+errorBody);
                }
            }

            @Override
            public void onFailure(Call<SuccessPojo> call, Throwable t) {
                Log.d("response:","fail:"+t.getMessage());
                M.hideLoadingDialog();
                // handle execution failures like no internet connectivity
            }
        });
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
}
