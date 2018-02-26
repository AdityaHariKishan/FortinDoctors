package com.doctor.app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.doctor.app.model.CityPojo;
import com.doctor.app.model.M;
import com.doctor.app.webservice.APIAuthentication;
import com.doctor.app.webservice.Service;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class SelectCity extends AppCompatActivity {
    
    ListView lv;
    String TAG="selectcity",parentact="";
    Context context;
    ArrayList<String> cities=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);
        
        context=SelectCity.this;

        lv=(ListView)findViewById(R.id.lvcity);
        if(getIntent().getAction()!=null)
            parentact=getIntent().getAction();
        getCity();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(context, "sel city:"+cities.get(position), Toast.LENGTH_SHORT).show();
                String city=cities.get(position);
                Intent it=new Intent(context,MainActivity.class);
                if(parentact.equals("settings"))
                    it.setAction("settings");
                M.setCity(city,context);
                finish();
                startActivity(it);
            }
        });
    }

    private void getCity() {

        M.showLoadingDialog(context);
        APIAuthentication mAuthenticationAPI = Service.createService(context,APIAuthentication.class);
        Call<List<CityPojo>> call = mAuthenticationAPI.getCities();
        call.enqueue(new retrofit2.Callback<List<CityPojo>>() {
            @Override
            public void onResponse(Call<List<CityPojo>> call, Response<List<CityPojo>> response) {
                Log.d("response:","data:"+response);
                // response.isSuccessful() is true if the response code is 2xx
                if (response.isSuccessful()) {
                    List<CityPojo> pojo = response.body();
                    if(pojo.size()>0){
                        cities.clear();
                        for(CityPojo data:pojo){
                            cities.add(data.getCity());
                        }
                        if(cities.size()>0){
                            ArrayAdapter<String> ada=new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,cities);
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
            public void onFailure(Call<List<CityPojo>> call, Throwable t) {
                Log.d("response:","fail:"+t.getMessage());
                M.hideLoadingDialog();
                // handle execution failures like no internet connectivity
            }
        });
    }

}
