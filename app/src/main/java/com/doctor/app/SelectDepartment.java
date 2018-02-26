package com.doctor.app;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.doctor.app.adapter.DepartmentAdapter;
import com.doctor.app.model.DepartmentPojo;
import com.doctor.app.model.M;
import com.doctor.app.webservice.APIAuthentication;
import com.doctor.app.webservice.Service;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class SelectDepartment extends Fragment {

    RecyclerView lv;
    Context context;
    View view;
    String TAG="SelectDepartment",city=null;
    ArrayList<DepartmentPojo> departments=new ArrayList<>();
    ArrayList<String> nmlist=new ArrayList<>();

    public SelectDepartment() {
        // Required empty public constructor
    }

    public static SelectDepartment newInstance(String param1, String param2) {
        SelectDepartment fragment = new SelectDepartment();
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
        view = inflater.inflate(R.layout.activity_select_department, container, false);

        context=getActivity();

        lv=(RecyclerView) view.findViewById(R.id.rvdept);
        lv.setLayoutManager(new GridLayoutManager(context,2));
        lv.setHasFixedSize(true);
        if(M.getCity(context)!=null)
            getDepartments();


//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                M.setDepartment(departments.get(position).getId(),context);
//                Intent it=new Intent(context,DoctorList.class);
//                startActivity(it);
//            }
//        });
        return view;
    }

    private void getDepartments() {
        city=M.getCity(context);
        M.showLoadingDialog(context);
        APIAuthentication mAuthenticationAPI = Service.createService(context,APIAuthentication.class);
        Call<List<DepartmentPojo>> call = mAuthenticationAPI.getDepartmentByCity(city);
        call.enqueue(new retrofit2.Callback<List<DepartmentPojo>>() {
            @Override
            public void onResponse(Call<List<DepartmentPojo>> call, Response<List<DepartmentPojo>> response) {
                Log.d("response:","data:"+response);
                if (response.isSuccessful()) {
                    List<DepartmentPojo> pojo = response.body();
                    if(pojo.size()>0){
                        departments.clear();
                        //nmlist.clear();
                        departments= (ArrayList<DepartmentPojo>) pojo;
//                        for(DepartmentPojo data:pojo){
//                            nmlist.add(data.getName());
//                        }
                        if(departments.size()>0){
//                            ArrayAdapter<String> ada=new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,nmlist);
//                            lv.setAdapter(ada);
                            Log.d(TAG,"department size:"+departments.size());
                            DepartmentAdapter ada=new DepartmentAdapter(departments,context);
                            lv.setAdapter(ada);
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
            public void onFailure(Call<List<DepartmentPojo>> call, Throwable t) {
                Log.d("response:","fail:"+t.getMessage());
                M.hideLoadingDialog();
                // handle execution failures like no internet connectivity
            }
        });
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if(item.getItemId()==android.R.id.home){
//            onBackPressed();
//        }
//        return super.onOptionsItemSelected(item);
//    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        finish();
//    }
}
