package com.doctor.app;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

public class Prescription extends Fragment {

    View view;
    Context context;
    String TAG="Prescription";
    RecyclerView rv;
    TextView tvnodata;
    DoctorAdapter adapter;

    public Prescription() {
        // Required empty public constructor
    }

    public static Prescription newInstance() {
        Prescription fragment = new Prescription();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_prescription, container, false);
        context=getActivity();
        rv=(RecyclerView)view.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(context));
        rv.setHasFixedSize(true);
        tvnodata=(TextView)view.findViewById(R.id.tvnodata);
        getData();
        return view;
    }

    private void getData() {

        M.showLoadingDialog(context);
        APIAuthentication mAuthenticationAPI = Service.createService(context,APIAuthentication.class);
        Call<List<DoctorPojo>> call = mAuthenticationAPI.doctorList(M.getID(context));
        call.enqueue(new retrofit2.Callback<List<DoctorPojo>>() {
            @Override
            public void onResponse(Call<List<DoctorPojo>> call, Response<List<DoctorPojo>> response) {
                Log.d("response:","data:"+response);
                // response.isSuccessful() is true if the response code is 2xx
                if (response.isSuccessful()) {
                    List<DoctorPojo> pojo = response.body();
                    if(pojo!=null && pojo.size()>0){
                            rv.setVisibility(View.VISIBLE);
                            tvnodata.setVisibility(View.GONE);
                            adapter=new DoctorAdapter(pojo,context,"Prescription");
                            rv.setAdapter(adapter);
                    }else{
                        rv.setVisibility(View.GONE);
                        tvnodata.setVisibility(View.VISIBLE);
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

}
