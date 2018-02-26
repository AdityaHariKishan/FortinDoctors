package com.doctor.app;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.doctor.app.adapter.DoctorAdapter;
import com.doctor.app.adapter.HospitalAdapter;
import com.doctor.app.helper.AppConst;
import com.doctor.app.model.DoctorPojo;
import com.doctor.app.model.HospitalPojo;

import java.util.ArrayList;

public class NearByHospital extends AppCompatActivity {

    RecyclerView rv;
    TextView tv;
    Context context;
    String TAG="NearByHospital";
   // HospitalAdapter adapter;
    DoctorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_by_hospital);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context=NearByHospital.this;

        tv=(TextView)findViewById(R.id.tvnodata);
        tv.setVisibility(View.GONE);
        rv=(RecyclerView)findViewById(R.id.rvlist);
        rv.setLayoutManager(new LinearLayoutManager(context));
        rv.setHasFixedSize(true);

        if (AppConst.hospitalPojoArrayList!=null){
            ArrayList<DoctorPojo> list=AppConst.hospitalPojoArrayList;
            if(list.size()>0){
                tv.setVisibility(View.GONE);
                rv.setVisibility(View.VISIBLE);
                adapter=new DoctorAdapter(list,context,"doctor");
                rv.setAdapter(adapter);
            }else{
                tv.setVisibility(View.VISIBLE);
                rv.setVisibility(View.GONE);
            }
        }else{
            tv.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
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
}
