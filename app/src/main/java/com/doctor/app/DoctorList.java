package com.doctor.app;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import com.doctor.app.adapter.DoctorAdapter;
import com.doctor.app.model.DoctorPojo;
import com.doctor.app.model.M;
import com.doctor.app.webservice.APIAuthentication;
import com.doctor.app.webservice.Service;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class DoctorList extends AppCompatActivity {

    RecyclerView rv;
    String TAG="DoctorList",city,department;
    Context context;
    DoctorAdapter adapter;
    ArrayList<DoctorPojo> list=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context=DoctorList.this;

        rv=(RecyclerView)findViewById(R.id.rvdoctors);
        rv.setLayoutManager(new LinearLayoutManager(context));
        rv.setHasFixedSize(true);

        if(M.getCity(context)!=null && M.getDepartment(context)!=null)
            getDoctors();
        else
            finish();
    }

    private void getDoctors() {
        city=M.getCity(context);
        department=M.getDepartment(context);
        M.showLoadingDialog(context);
        APIAuthentication mAuthenticationAPI = Service.createService(context,APIAuthentication.class);
        Call<List<DoctorPojo>> call = mAuthenticationAPI.getDoctors(city,department);
        call.enqueue(new retrofit2.Callback<List<DoctorPojo>>() {
            @Override
            public void onResponse(Call<List<DoctorPojo>> call, Response<List<DoctorPojo>> response) {
                Log.d("response:","data:"+response);
                // response.isSuccessful() is true if the response code is 2xx
                if (response.isSuccessful()) {
                    List<DoctorPojo> pojo = response.body();
                    if(pojo.size()>0){
                       list= (ArrayList<DoctorPojo>) pojo;
                        if(list.size()>0){
                            adapter=new DoctorAdapter(list,context,"doctor");
                            rv.setAdapter(adapter);
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
            public void onFailure(Call<List<DoctorPojo>> call, Throwable t) {
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
