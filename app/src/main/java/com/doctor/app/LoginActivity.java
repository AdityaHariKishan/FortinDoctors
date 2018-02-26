package com.doctor.app;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.doctor.app.model.M;
import com.doctor.app.model.SuccessPojo;
import com.doctor.app.model.User;
import com.doctor.app.model.UserPojo;
import com.doctor.app.webservice.APIAuthentication;
import com.doctor.app.webservice.Service;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    String TAG= LoginActivity.class.getSimpleName();
    Button login, register,fb;
    TextView forgot;
    EditText etemail,etpwd;
    LoginButton fb_login;
    String email,pwd;
    Dialog dialog;
    //Facebook
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(LoginActivity.this);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();
        login = (Button) findViewById(R.id.login);
        register = (Button) findViewById(R.id.register);
        etemail=(EditText)findViewById(R.id.etusremail);
        etpwd=(EditText)findViewById(R.id.etusrpwd);
        forgot=(TextView)findViewById(R.id.tvresetpwd);
        fb_login=(LoginButton)findViewById(R.id.fb_login);
        fb=(Button)findViewById(R.id.btnSignFacebook);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.doctor.app",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        login.setOnClickListener(this);
        register.setOnClickListener(this);
        forgot.setOnClickListener(this);
        fb.setOnClickListener(this);

        fb_login.setReadPermissions("public_profile","email");
        fb_login.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        // App code
                        M.showLoadingDialog(LoginActivity.this);
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject json, GraphResponse response) {
                                        if (response.getError() != null) {
                                            Log.d(TAG,"error:"+ response.getError().toString());
                                            Toast.makeText(getApplicationContext(), response.getError().toString(), Toast.LENGTH_SHORT);
                                        } else {
                                            M.hideLoadingDialog();
                                            json = response.getJSONObject();
                                            Log.d(TAG,"response json:"+response);
                                            String name,mail,gender,logintype,profileid;
                                            try {
                                                profileid=json.getString("id");
                                                if(!json.isNull("name"))
                                                    name = json.getString("name");
                                                else
                                                    name="";
                                                if(!json.isNull("email"))
                                                    mail = json.getString("email");
                                                else
                                                    mail="";
                                                if(!json.isNull("gender")) {
                                                    gender = json.getString("gender");
                                                    gender = gender.substring(0,1).toUpperCase() + gender.substring(1).toLowerCase();
                                                }
                                                else
                                                    gender="";
                                                logintype="facebook";
                                                Log.d(TAG,"fb name:"+name);
                                                Log.d(TAG,"fb email:"+mail);
                                                Log.d(TAG,"fb gender:"+gender);
                                                social_login(name,mail,gender,logintype);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                Log.d(TAG,"facebook error:"+e.getMessage());
                                            }catch (Exception e){
                                                e.printStackTrace();
                                                Log.d(TAG,"facebook error:"+e.getMessage());
                                            }
                                        }

                                    }

                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        System.out.println("onCancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        System.out.println("exception--" + exception.toString());
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.login){
            if(etemail.getText().toString().trim().length()<=0){
                etemail.setError("Email Required");
            }else if(!isValidEmaillId(etemail.getText().toString())){
                etemail.setError("Invalid Email");
            }else if(etpwd.getText().toString().trim().length()<=0){
                etpwd.setError("Password Required");
            }else{
                email=etemail.getText().toString();
                pwd=etpwd.getText().toString();
                Log.d(TAG,email+" "+pwd);
                login();
            }
        }else if(v.getId()==R.id.register){
            Intent it=new Intent(LoginActivity.this,RegisterActivity.class);
            startActivity(it);
        }else if(v.getId()==R.id.tvresetpwd){
            dialog = new Dialog(LoginActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog_forgot_pwd);
            final EditText edtemail=(EditText) dialog.findViewById(R.id.etemailaddress);
            TextView btn=(TextView) dialog.findViewById(R.id.btnforgot);
            TextView btncancel=(TextView) dialog.findViewById(R.id.btncancel);

            btncancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.cancel();
                }
            });

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(edtemail.getText().toString().trim().length()<=0)
                        edtemail.setError("Email Required");
                    else if(!isValidEmaillId(edtemail.getText().toString())){
                        edtemail.setError("Invalid Email");
                    }else{
                        String mail=edtemail.getText().toString();
                        forgotPwd(mail);
                    }
                }
            });
            dialog.show();
        }else if(v.getId()==R.id.btnSignFacebook){
            fb_login.performClick();
        }
    }

    public void social_login(String name,String mail,String gender,String login_type){
        M.showLoadingDialog(LoginActivity.this);
        Log.d("facebook",mail+" "+name+" "+gender+" "+login_type);

        RequestBody m = RequestBody.create(MediaType.parse("text/plain"), mail);
        RequestBody n = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody g = RequestBody.create(MediaType.parse("text/plain"), gender);
        RequestBody l = RequestBody.create(MediaType.parse("text/plain"), login_type);
        RequestBody c = RequestBody.create(MediaType.parse("text/plain"), "");
        RequestBody p = RequestBody.create(MediaType.parse("text/plain"), "");
        RequestBody ph = RequestBody.create(MediaType.parse("text/plain"), "");
        APIAuthentication mAuthenticationAPI = Service.createService(LoginActivity.this,APIAuthentication.class);
        Call<UserPojo> call = mAuthenticationAPI.register(n,p,m,ph,g,c,l,null);
        call.enqueue(new Callback<UserPojo>() {
            @Override
            public void onResponse(Call<UserPojo> call, Response<UserPojo> response) {
                Log.d("response:","data:"+response);
                // response.isSuccessful() is true if the response code is 2xx
                if (response.isSuccessful()) {
                    UserPojo pojo = response.body();
                    if(pojo!=null){
                    Log.d(TAG,"res:"+pojo.getSuccess());
                        M.hideLoadingDialog();
                        if(pojo.getSuccess()==1 || pojo.getSuccess()==0) {
                            if (pojo.getUser() != null) {
                                User user = pojo.getUser();
                                M.setID(user.getId() + "", LoginActivity.this);
                                M.setUsername(user.getName(), LoginActivity.this);
                                M.setEmail(user.getEmail(), LoginActivity.this);
                                M.setPhone(user.getMobile_no(), LoginActivity.this);
                                M.setlogintype(user.getLogin_type(), LoginActivity.this);
                                M.setRole(pojo.getRole(),LoginActivity.this);
                                if(pojo.getRole().equals("doctor")){
                                    M.setStatus(user.getAvailbility_status(),LoginActivity.this);
                                    Intent it = new Intent(LoginActivity.this, MainActivity.class);
                                    finish();
                                    startActivity(it);
                                }else {
                                    M.setGender(user.getGender(), LoginActivity.this);
                                    Intent it = new Intent(LoginActivity.this, SelectCity.class);
                                    finish();
                                    startActivity(it);
                                }
//                                Intent it = new Intent(LoginActivity.this, SelectCity.class);
//                                finish();
//                                startActivity(it);
                            }

                        }else{
                            if(pojo.getMessage()!=null){
                                Toast.makeText(LoginActivity.this,pojo.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    }else{
                        M.hideLoadingDialog();
                    }
                } else {
                    int statusCode = response.code();
                    ResponseBody errorBody = response.errorBody();
                }
            }

            @Override
            public void onFailure(Call<UserPojo> call, Throwable t) {
                Log.d("response:","fail:"+t.getMessage());
                // handle execution failures like no internet connectivity
            }
        });
    }

    private void login(){
        M.showLoadingDialog(LoginActivity.this);
        APIAuthentication mAuthenticationAPI = Service.createService(LoginActivity.this,APIAuthentication.class);
        Call<UserPojo> call = mAuthenticationAPI.login(email,pwd);
        call.enqueue(new Callback<UserPojo>() {
            @Override
            public void onResponse(Call<UserPojo> call, Response<UserPojo> response) {
                Log.d("response:","data:"+response);
                // response.isSuccessful() is true if the response code is 2xx
                if (response.isSuccessful()) {
                    UserPojo pojo=response.body();
                    if(pojo!=null){
                    if(pojo.getSuccess()==1) {
                        if(pojo.getUser()!=null) {
                            User user=pojo.getUser();
                            M.setID(user.getId()+"", LoginActivity.this);
                            M.setUsername(user.getName(), LoginActivity.this);
                            M.setEmail(user.getEmail(), LoginActivity.this);
                            M.setPhone(user.getMobile_no(), LoginActivity.this);
                            M.setlogintype(user.getLogin_type(), LoginActivity.this);
                            M.setRole(pojo.getRole(),LoginActivity.this);
                            M.hideLoadingDialog();
                            if(pojo.getRole().equals("doctor")){
                                M.setPhoto(user.getDoctor_image(),LoginActivity.this);
                                M.setStatus(user.getAvailbility_status(),LoginActivity.this);
                                Intent it = new Intent(LoginActivity.this, MainActivity.class);
                                finish();
                                startActivity(it);
                            }else {
                                M.setPhoto(user.getPhoto(),LoginActivity.this);
                                M.setGender(user.getGender(), LoginActivity.this);
                                Intent it = new Intent(LoginActivity.this, SelectCity.class);
                                finish();
                                startActivity(it);
                            }
                        }

                    }else{
                        Toast.makeText(LoginActivity.this,"Invalid Email or Password...",Toast.LENGTH_SHORT).show();
                    }
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
            public void onFailure(Call<UserPojo> call, Throwable t) {
                Log.d("response:","fail:"+t.getMessage());
                M.hideLoadingDialog();
                // handle execution failures like no internet connectivity
            }
        });
    }

    public void forgotPwd(String mail){
        Log.d(TAG,"mail:"+mail);
        M.showLoadingDialog(LoginActivity.this);
        APIAuthentication mAuthenticationAPI = Service.createService(LoginActivity.this,APIAuthentication.class);
        Call<SuccessPojo> call = mAuthenticationAPI.forgotPassword(mail);
        call.enqueue(new Callback<SuccessPojo>() {
            @Override
            public void onResponse(Call<SuccessPojo> call, Response<SuccessPojo> response) {
                Log.d("response:","data:"+response);
                // response.isSuccessful() is true if the response code is 2xx
                if (response.isSuccessful()) {
                    SuccessPojo pojo = response.body();
                    if(pojo!=null){
                    if(pojo.getSuccess().equals("1")){
                        Toast.makeText(LoginActivity.this,"Password Sent via email.Please Check youe email.",Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }else{
                        Toast.makeText(LoginActivity.this,"Invalid Email",Toast.LENGTH_SHORT).show();
                    }
                }
                } else {
                    int statusCode = response.code();

                    // handle request errors yourself
                    ResponseBody errorBody = response.errorBody();
                }
                M.hideLoadingDialog();
            }

            @Override
            public void onFailure(Call<SuccessPojo> call, Throwable t) {
                Log.d(TAG,"fail:"+t.getMessage());
                M.hideLoadingDialog();
                // handle execution failures like no internet connectivity
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    private boolean isValidEmaillId(String email){

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
    }


    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {

                return true;
            } else {
                ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},2);
                return false;
            }
        }else {
            return true;
        }
    }
}
