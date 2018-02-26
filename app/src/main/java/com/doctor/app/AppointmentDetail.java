package com.doctor.app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.doctor.app.helper.AppConst;
import com.doctor.app.model.AppointmentPojo;
import com.doctor.app.model.M;
import com.doctor.app.model.SuccessPojo;
import com.doctor.app.view.CropSquareTransformation;
import com.doctor.app.webservice.DoctorAPI;
import com.doctor.app.webservice.Service;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentDetail extends AppCompatActivity implements View.OnClickListener {

    TextView tvname,tvnote,tvstatus;
    RelativeTimeTextView tvtime;
    ImageView iv;
    Button btncancel;
    
    String TAG="AppointmentDetail";
    Context context;
    AppointmentPojo model;
    SimpleDateFormat defaultfmt=new SimpleDateFormat("yyyy-MM-dd HH:mm");
    SimpleDateFormat dtfmt=new SimpleDateFormat("dd MMM yyyy, hh:mm a");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        model=AppConst.selAppointment;
        context=AppointmentDetail.this;
        initview();
    }

    private void initview() {
        tvname = (TextView) findViewById(R.id.tvdrname);
        tvtime = (RelativeTimeTextView) findViewById(R.id.tvtime);
        tvnote = (TextView) findViewById(R.id.tvnote);
        tvstatus = (TextView) findViewById(R.id.tvstatus);
        iv = (ImageView) findViewById(R.id.ivdoctor);
        btncancel=(Button)findViewById(R.id.btncancel);
        btncancel.setOnClickListener(this);
        tvname.setText(model.getDoctor_name());

        String t=model.getAppointment_date()+" "+model.getAppointment_time();
        Log.d("time---", ""+t);
        Date dt=null;
        try {
            dt=defaultfmt.parse(t);
            tvtime.setText(dtfmt.format(dt));
//            tvtime.setReferenceTime(dt.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String note=model.getNote();
        if(note!=null){
            if(note.trim().length()>0) {
                tvnote.setVisibility(View.VISIBLE);
                tvnote.setText(note);
            }else
                tvnote.setVisibility(View.GONE);
        }else{
            tvnote.setVisibility(View.GONE);
        }
        String pic=model.getDoctor_image();
        if(pic.trim().length()>0){
            Picasso.with(context)
                    .load(AppConst.doctor_img_url + pic)
                    .transform(new CropSquareTransformation())
                    .placeholder(R.drawable.ic_user)
                    .error(R.drawable.ic_user)
                    .into(iv);
        }

        String status=model.getAppointment_status();
        tvstatus.setVisibility(View.VISIBLE);
        tvstatus.setText(status);
        if(status.equals("pending")){
            btncancel.setVisibility(View.VISIBLE);
            tvstatus.setTextColor(getResources().getColor(android.R.color.holo_blue_bright));
        }else if(status.equals("accepted")){
            btncancel.setVisibility(View.VISIBLE);
            tvstatus.setTextColor(getResources().getColor(android.R.color.holo_green_light));
        }else{
            btncancel.setVisibility(View.GONE);
            tvstatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }
    }

    @Override
    public void onClick(View v) {
        if(v==btncancel){
            updateStatus(AppConst.status_cancel);
        }
    }

    private void updateStatus(final String status){

        M.showLoadingDialog(context);
        DoctorAPI mAuthenticationAPI = Service.createService(context,DoctorAPI.class);
        Call<SuccessPojo> call = mAuthenticationAPI.updateStatus(model.getDoctor_id(),model.getId(),status,null);
        call.enqueue(new Callback<SuccessPojo>() {
            @Override
            public void onResponse(Call<SuccessPojo> call, Response<SuccessPojo> response) {
                Log.d(TAG,"response:"+"data:"+response);
                if (response.isSuccessful()) {
                    SuccessPojo pojo=response.body();
                    if(pojo.getSuccess().equals("1")){
                        Intent it=new Intent(context, MainActivity.class);
                        it.setAction("appointment");
                        finish();
                        startActivity(it);
                    }else{
                        Log.d(TAG,"fail:"+pojo.getSuccess());
                    }

                } else {
                    int statusCode = response.code();
                    ResponseBody errorBody = response.errorBody();
                    Log.d(TAG,"error:"+statusCode+" "+errorBody);
                }
                M.hideLoadingDialog();
            }

            @Override
            public void onFailure(Call<SuccessPojo> call, Throwable t) {
                Log.d(TAG,"response:"+"fail:"+t.getMessage());
                M.hideLoadingDialog();
            }
        });
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
