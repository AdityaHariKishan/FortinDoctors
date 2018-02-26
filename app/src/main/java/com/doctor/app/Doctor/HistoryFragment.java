package com.doctor.app.Doctor;

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

import com.doctor.app.R;
import com.doctor.app.adapter.AppointmentAdapter;
import com.doctor.app.adapter.CurrentAppointmentAdapter;
import com.doctor.app.adapter.UpcomingAppointmentAdapter;
import com.doctor.app.model.AppointmentPojo;
import com.doctor.app.model.M;
import com.doctor.app.webservice.DoctorAPI;
import com.doctor.app.webservice.Service;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryFragment extends Fragment {

    View view;
    RecyclerView rv;
    TextView tvnodata;
    Context context;
    String TAG="HistoryFragment";
    UpcomingAppointmentAdapter adapter;

    public HistoryFragment() {

    }

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_history, container, false);
        context=getActivity();
        tvnodata=(TextView)view.findViewById(R.id.tvnodata);
        rv=(RecyclerView)view.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(context));
        rv.setHasFixedSize(true);

        getData();
        return view;
    }

    private void getData(){
        DoctorAPI mAuthenticationAPI = Service.createService(context,DoctorAPI.class);
        Call<List<AppointmentPojo>> call = mAuthenticationAPI.getHistory(M.getID(context));
        call.enqueue(new Callback<List<AppointmentPojo>>() {
            @Override
            public void onResponse(Call<List<AppointmentPojo>> call, Response<List<AppointmentPojo>> response) {
                Log.d("response:","data:"+response);
                if (response.isSuccessful()) {
                    List<AppointmentPojo> pojo=response.body();
                    if(pojo!=null && pojo.size()>0){
                        rv.setVisibility(View.VISIBLE);
                        tvnodata.setVisibility(View.GONE);
                        adapter=new UpcomingAppointmentAdapter(pojo,context);
                        rv.setAdapter(adapter);
                    }else{
                        rv.setVisibility(View.GONE);
                        tvnodata.setVisibility(View.VISIBLE);
                    }

                } else {
                    int statusCode = response.code();
                    ResponseBody errorBody = response.errorBody();
                    Log.d(TAG,"error:"+statusCode+" "+errorBody);
                }
            }

            @Override
            public void onFailure(Call<List<AppointmentPojo>> call, Throwable t) {
                Log.d("response:","fail:"+t.getMessage());
            }
        });
    }

}
