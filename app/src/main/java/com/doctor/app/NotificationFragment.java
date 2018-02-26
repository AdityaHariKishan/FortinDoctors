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
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.doctor.app.adapter.NotificationAdapter;
import com.doctor.app.model.NotificationPojo;
import com.doctor.app.model.M;
import com.doctor.app.model.NotificationPojo;
import com.doctor.app.webservice.APIAuthentication;
import com.doctor.app.webservice.Service;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class NotificationFragment extends Fragment {

    RecyclerView rv;
    TextView tv;
    View view;
    Context context;

    public NotificationFragment() {
        // Required empty public constructor
    }


    public static NotificationFragment newInstance(String param1, String param2) {
        NotificationFragment fragment = new NotificationFragment();
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
        view=inflater.inflate(R.layout.fragment_notification, container, false);
        context=getActivity();
        
        tv=(TextView)view.findViewById(R.id.tv);
        tv.setVisibility(View.GONE);
        rv=(RecyclerView)view.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(context));
        rv.setHasFixedSize(true);
        
        getData();
        return view;
    }

    private void getData() {

        M.showLoadingDialog(context);
        APIAuthentication mAuthenticationAPI = Service.createService(context,APIAuthentication.class);
        Call<List<NotificationPojo>> call = mAuthenticationAPI.getData(M.getID(context),M.getRole(context));
        call.enqueue(new retrofit2.Callback<List<NotificationPojo>>() {
            @Override
            public void onResponse(Call<List<NotificationPojo>> call, Response<List<NotificationPojo>> response) {
                Log.d("response:","data:"+response);
                // response.isSuccessful() is true if the response code is 2xx
                if (response.isSuccessful()) {
                    List<NotificationPojo> pojo = response.body();
                    if(pojo.size()>0){
                        NotificationAdapter adapter=new NotificationAdapter(pojo,context);
                        rv.setAdapter(adapter);
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
            public void onFailure(Call<List<NotificationPojo>> call, Throwable t) {
                Log.d("response:","fail:"+t.getMessage());
                M.hideLoadingDialog();
                // handle execution failures like no internet connectivity
            }
        });
    }


}
