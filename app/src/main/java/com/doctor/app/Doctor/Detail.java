package com.doctor.app.Doctor;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.doctor.app.AppointmentDetail;
import com.doctor.app.MainActivity;
import com.doctor.app.R;
import com.doctor.app.adapter.UpcomingAppointmentAdapter;
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
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Detail extends AppCompatActivity implements View.OnClickListener {

    TextView tvname,tvnote,tvstatus;
    RelativeTimeTextView tvtime;
    ImageView iv;
    Button btncancel,btncomplete,btnaccept;

    String TAG="Detail",desc=null;
    Context context;
    AppointmentPojo model;
    SimpleDateFormat defaultfmt=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat dtfmt=new SimpleDateFormat("dd MMM yyyy, hh:mm a");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        model= AppConst.selAppointment;
        context=Detail.this;
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
        btncomplete=(Button)findViewById(R.id.btncomplete);
        btncomplete.setOnClickListener(this);
        btnaccept=(Button)findViewById(R.id.btnaccept);
        btnaccept.setOnClickListener(this);
        tvname.setText(model.getName());
        String t=model.getAppointment_date()+" "+model.getAppointment_time();
        Date dt=null;
        try {
            dt=defaultfmt.parse(t);
            //tvtime.setText(dtfmt.format(dt));
            tvtime.setReferenceTime(dt.getTime());
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
        String pic=model.getPhoto();
        if(pic==null)
            pic="";
        if(pic.trim().length()>0){
            Picasso.with(context)
                    .load(AppConst.profile_img_url + pic)
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
            btnaccept.setVisibility(View.VISIBLE);
            btncomplete.setVisibility(View.GONE);
            tvstatus.setTextColor(getResources().getColor(android.R.color.holo_blue_bright));
        }else if(status.equals("accepted")){
            btnaccept.setVisibility(View.GONE);
            btncancel.setVisibility(View.VISIBLE);
            btncomplete.setVisibility(View.VISIBLE);
            tvstatus.setTextColor(getResources().getColor(android.R.color.holo_green_light));
        }else{
            btnaccept.setVisibility(View.GONE);
            btncancel.setVisibility(View.GONE);
            btncomplete.setVisibility(View.GONE);
            tvstatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }
    }

    @Override
    public void onClick(View v) {
        if(v==btncancel){
            desc=null;
            updateStatus(AppConst.status_cancel);
        }else if(v==btncomplete){
            final Dialog dialog=new Dialog(context,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
            dialog.setCancelable(false);
            dialog.getWindow().setContentView(R.layout.dialog_status_complete);

            final EditText etdesc=(EditText)dialog.findViewById(R.id.etdesc);
            Button btnsubmit=(Button)dialog.findViewById(R.id.btnsubmit);

            btnsubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(etdesc.getText().toString().trim().length()<=0)
                        desc="";
                    else
                        desc=etdesc.getText().toString();
                    dialog.dismiss();
                    updateStatus(AppConst.status_complete);

                }
            });

            dialog.show();
        }else if(v==btnaccept){
            desc=null;
            updateStatus(AppConst.status_accept);
        }
    }

    private void updateStatus(final String status){
        M.showLoadingDialog(context);
        DoctorAPI mAuthenticationAPI = Service.createService(context,DoctorAPI.class);
        Call<SuccessPojo> call = mAuthenticationAPI.updateStatus(M.getID(context),model.getId(),status,desc);
        call.enqueue(new Callback<SuccessPojo>() {
            @Override
            public void onResponse(Call<SuccessPojo> call, Response<SuccessPojo> response) {
                Log.d("response:","data:"+response);
                if (response.isSuccessful()) {
                    SuccessPojo pojo=response.body();
                    if(pojo.getSuccess().equals("1")){
                        Intent it=new Intent(context, MainActivity.class);
                        finish();
                        startActivity(it);
                    }else{

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
                Log.d("response:","fail:"+t.getMessage());
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
