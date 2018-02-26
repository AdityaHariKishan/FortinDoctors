package com.doctor.app;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.doctor.app.adapter.PrescriptionAdapter;
import com.doctor.app.adapter.TimingAdapter;
import com.doctor.app.adapter.ViewPagerAdapter;
import com.doctor.app.helper.AppConst;
import com.doctor.app.model.DepartmentPojo;
import com.doctor.app.model.DoctorDetailPojo;
import com.doctor.app.model.DoctorPojo;
import com.doctor.app.model.M;
import com.doctor.app.model.PrescriptionPojo;
import com.doctor.app.view.CropSquareTransformation;
import com.doctor.app.webservice.APIAuthentication;
import com.doctor.app.webservice.Service;
import com.squareup.picasso.Picasso;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class PrescriptionDetail extends AppCompatActivity {

    TextView tvdrname,tvcname,tvcph,tvphone;
    RecyclerView rv;
    Context context;
    String TAG="PrescriptionDetail",doctor_id;
    PrescriptionAdapter adapter;
    DoctorPojo doctorPojo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context=PrescriptionDetail.this;
        doctorPojo= AppConst.seldr;
        doctor_id=doctorPojo.getId();
        tvdrname=(TextView)findViewById(R.id.tvdrname);
        tvcname=(TextView)findViewById(R.id.tvclinic);
        tvcph=(TextView)findViewById(R.id.tvclinicph);
        tvphone=(TextView)findViewById(R.id.tvphone);
        rv=(RecyclerView)findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(context));
        rv.setHasFixedSize(true);
        rv.setNestedScrollingEnabled(false);
        tvdrname.setText(doctorPojo.getDoctor_name());
        tvcname.setText(doctorPojo.getClinic_name());

       getDoctorDetail();
    }

    private void getDoctorDetail() {

        M.showLoadingDialog(context);
        APIAuthentication mAuthenticationAPI = Service.createService(context,APIAuthentication.class);
        Call<DoctorDetailPojo> call = mAuthenticationAPI.getDoctorDetail(doctor_id);
        call.enqueue(new retrofit2.Callback<DoctorDetailPojo>() {
            @Override
            public void onResponse(Call<DoctorDetailPojo> call, Response<DoctorDetailPojo> response) {
                if (response.isSuccessful()) {
                    DoctorDetailPojo pojo = response.body();
                    String clinicnm="",doctornm="",address="",contact="",drphone="",abt="",drimg="";
                    if(pojo!=null){

                        contact=pojo.getClinic_phone();
                        drphone=pojo.getPersonal_phone();

                        if(drphone.trim().length()<=0){
                            tvphone.setVisibility(View.GONE);
                        }else {
                            tvphone.setVisibility(View.VISIBLE);
                            tvphone.setText("Phone: "+drphone);
                        }
                        if(contact.trim().length()<=0){
                            tvcph.setVisibility(View.GONE);
                        }else {
                            tvcph.setVisibility(View.VISIBLE);
                            tvcph.setText("Contact Number: "+contact);
                        }

                    }
                    M.hideLoadingDialog();
                    getData();
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

    private void getData() {

        M.showLoadingDialog(context);
        APIAuthentication mAuthenticationAPI = Service.createService(context,APIAuthentication.class);
        Call<List<PrescriptionPojo>> call = mAuthenticationAPI.getPrescriptions(M.getID(context),doctor_id);
        call.enqueue(new retrofit2.Callback<List<PrescriptionPojo>>() {
            @Override
            public void onResponse(Call<List<PrescriptionPojo>> call, Response<List<PrescriptionPojo>> response) {
                Log.d("response:","data:"+response);
                // response.isSuccessful() is true if the response code is 2xx
                if (response.isSuccessful()) {
                    List<PrescriptionPojo> pojo = response.body();
                    if(pojo.size()>0){
                        if(pojo.size()>0){
                            adapter=new PrescriptionAdapter(pojo,context);
                            rv.setAdapter(adapter);
                        }
                    }
                    M.hideLoadingDialog();
                } else {
                    int statusCode = response.code();
                    M.hideLoadingDialog();
                    ResponseBody errorBody = response.errorBody();
                }
            }

            @Override
            public void onFailure(Call<List<PrescriptionPojo>> call, Throwable t) {
                Log.d("response:","fail:"+t.getMessage());
                M.hideLoadingDialog();
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
