package com.doctor.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.doctor.app.adapter.AppointmentAdapter;
import com.doctor.app.model.AppointmentPojo;
import com.doctor.app.model.M;
import com.doctor.app.webservice.APIAuthentication;
import com.doctor.app.webservice.Service;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Appointments extends Fragment {

    View view;
    RecyclerView rv;
    TextView tvnodata;
    
    Context context;
    String TAG="Appointments";
    AppointmentAdapter adapter;

    public Appointments() {
        // Required empty public constructor
    }

    public static Appointments newInstance(String param1, String param2) {
        Appointments fragment = new Appointments();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_appointments, container, false);
        context=getActivity();
        
        tvnodata=(TextView)view.findViewById(R.id.tvnodata);
        rv=(RecyclerView)view.findViewById(R.id.rvappointment);
        rv.setLayoutManager(new LinearLayoutManager(context));
        rv.setHasFixedSize(true);
        
        getAppointments();
        return view;
    }

    private void getAppointments(){
        M.showLoadingDialog(context);
        APIAuthentication mAuthenticationAPI = Service.createService(context,APIAuthentication.class);
        Call<List<AppointmentPojo>> call = mAuthenticationAPI.getAppointments(M.getID(context));
        call.enqueue(new Callback<List<AppointmentPojo>>() {
            @Override
            public void onResponse(Call<List<AppointmentPojo>> call, Response<List<AppointmentPojo>> response) {
                Log.d("response:","data:"+response);
                // response.isSuccessful() is true if the response code is 2xx
                if (response.isSuccessful()) {
                    List<AppointmentPojo> pojo=response.body();
                    if(pojo!=null && pojo.size()>0){
                        rv.setVisibility(View.VISIBLE);
                        tvnodata.setVisibility(View.GONE);
                        adapter=new AppointmentAdapter(pojo,context);
                        rv.setAdapter(adapter);
                    }else{
                        rv.setVisibility(View.GONE);
                        tvnodata.setVisibility(View.VISIBLE);
                    }

                } else {
                    int statusCode = response.code();

                    // handle request errors yourself
                    ResponseBody errorBody = response.errorBody();
                    Log.d(TAG,"error:"+statusCode+" "+errorBody);
                }
                M.hideLoadingDialog();
            }

            @Override
            public void onFailure(Call<List<AppointmentPojo>> call, Throwable t) {
                Log.d("response:","fail:"+t.getMessage());
                M.hideLoadingDialog();
                // handle execution failures like no internet connectivity
            }
        });
    }

}
