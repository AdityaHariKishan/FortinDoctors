package com.doctor.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.doctor.app.model.M;
import com.doctor.app.model.SuccessPojo;
import com.doctor.app.webservice.APIAuthentication;
import com.doctor.app.webservice.Service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class ChangePassword extends AppCompatActivity {

    EditText etoldpwd,etnewpwd,etconfirmpwd;
    Button btnchange;

    Context context;
    String TAG="Change Password",userid,currpwd,newpwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context=ChangePassword.this;
        userid= M.getID(context);

        etoldpwd=(EditText)findViewById(R.id.etoldpwd);
        etconfirmpwd=(EditText)findViewById(R.id.etconfirmpwd);
        etnewpwd=(EditText)findViewById(R.id.etnewpwd);
        btnchange=(Button)findViewById(R.id.btnchange);

        btnchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etoldpwd.getText().toString().trim().length()<=0){
                    etoldpwd.setError(getString(R.string.error_no_old_pwd));
                }else if(etnewpwd.getText().toString().trim().length()<=0){
                    etnewpwd.setError(getString(R.string.error_no_new_pwd));
                }else if(etconfirmpwd.getText().toString().trim().length()<=0){
                    etconfirmpwd.setError(getString(R.string.error_no_confirm_pwd));
                }else if(!etnewpwd.getText().toString().equals(etconfirmpwd.getText().toString())){
                    etconfirmpwd.setError(getString(R.string.error_mismatch_pwd));
                }else{
                    currpwd=etoldpwd.getText().toString();
                    newpwd=etnewpwd.getText().toString();

                    changepwd();
                }
            }
        });
    }

    private void changepwd() {

        M.showLoadingDialog(context);
        APIAuthentication mAuthenticationAPI = Service.createService(context,APIAuthentication.class);
        Call<SuccessPojo> call = mAuthenticationAPI.changePassword(userid,currpwd,newpwd);
        call.enqueue(new retrofit2.Callback<SuccessPojo>() {
            @Override
            public void onResponse(Call<SuccessPojo> call, Response<SuccessPojo> response) {
                //Log.d("response:","data:"+response);
                M.hideLoadingDialog();
                if (response.isSuccessful()) {
                    SuccessPojo pojo=response.body();
                    if(pojo!=null){
                        if(pojo.getSuccess().equals("1")){
                            Toast.makeText(context, R.string.success_change_pwd,Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }else{
                            Toast.makeText(context, R.string.error_pwd_incorrect,Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {
                    int statusCode = response.code();

                    // handle request errors yourself
                    ResponseBody errorBody = response.errorBody();
                    Log.d(TAG,"error:"+statusCode+" "+errorBody);
                }
            }

            @Override
            public void onFailure(Call<SuccessPojo> call, Throwable t) {
                Log.d("response:","fail:"+t.getMessage());
                M.hideLoadingDialog();
                // handle execution failures like no internet connectivity
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
