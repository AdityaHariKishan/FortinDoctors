package com.doctor.app;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.doctor.app.helper.FilePath;
import com.doctor.app.model.M;
import com.doctor.app.model.User;
import com.doctor.app.model.UserPojo;
import com.doctor.app.view.CropSquareTransformation;
import com.doctor.app.webservice.APIAuthentication;
import com.doctor.app.webservice.Service;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView ivprofile;
    Button register;
    EditText etname,etemail,etpwd,etphone,etcity;
    RadioGroup rg;
    RadioButton rbmale,rbfemale;
    TextView tvlogin;

    Intent mIntent;
    int RESULT_LOAD_IMAGE1=1;

    String TAG="Register",profile=null;
    String email="", name="", pwd="", phone="", gender="", login_type="email",city="";

    File file=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initUI();

    }

    private void initUI() {
        etname=(EditText)findViewById(R.id.etusername);
        etemail=(EditText)findViewById(R.id.etemail);
        etpwd=(EditText)findViewById(R.id.etpwd);
        etphone=(EditText)findViewById(R.id.etphone);
        etcity=(EditText)findViewById(R.id.etcity);
        rg=(RadioGroup)findViewById(R.id.rg);
        rbfemale=(RadioButton)findViewById(R.id.rbfemale);
        rbmale=(RadioButton)findViewById(R.id.rbmale);
        ivprofile=(ImageView)findViewById(R.id.ivprofile);
        register=(Button) findViewById(R.id.register);
        tvlogin=(TextView)findViewById(R.id.txtSignin);

        ivprofile.setOnClickListener(this);
        register.setOnClickListener(this);
        tvlogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.ivprofile){
            isStoragePermissionGranted();
//            mIntent = new Intent();
//            mIntent.setType("image/*");
//            mIntent.setAction(Intent.ACTION_GET_CONTENT);
//            startActivityForResult(
//                    Intent.createChooser(mIntent, "Select Picture"),
//                    RESULT_LOAD_IMAGE1);
        }else if(v.getId()==R.id.register){
            if(etname.getText().toString().trim().length()<=0){
                etname.setError("Username required");
            }else if(etemail.getText().toString().trim().length()<=0){
                etemail.setError("Email Required");
            }else if(!isValidEmaillId(etemail.getText().toString())){
                etemail.setError("Invalid Email");
            }else if(etpwd.getText().toString().trim().length()<=0){
                etpwd.setError("Password Required");
            }else{
                email=etemail.getText().toString();
                pwd=etpwd.getText().toString();
                name=etname.getText().toString();
                city="";//etcity.getText().toString();
                if(rbmale.isChecked())
                    gender="Male";
                else if(rbfemale.isChecked())
                    gender="Female";

                if(etphone.getText().toString().trim().length()>0)
                    phone=etphone.getText().toString();


//                Log.d(TAG,email+" "+pwd+" "+name);
//                Log.d(TAG,"profile:"+profile);
//                Log.d(TAG,"phone:"+phone+" "+gender+" "+login_type);
//                Log.d(TAG,"lat:"+latitude+" lng:"+longitude+" "+status);
//                Log.d(TAG,"file:"+file);
               register();
            }
        }else if(v.getId()==R.id.txtSignin){
            onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == this.RESULT_OK && requestCode == RESULT_LOAD_IMAGE1) {
            profile = FilePath.getPath(RegisterActivity.this, data.getData());

            Picasso.with(RegisterActivity.this)
                    .load(data.getData())
                    .transform(new CropSquareTransformation())
                    .placeholder(R.drawable.ic_user)
                    .error(R.drawable.ic_user)
                    .into(ivprofile);
            Bitmap imgbitmap=getBitmap(profile);
        }
    }

    private Bitmap getBitmap(String path) {
        int IMAGE_MAX_SIZE = 150000;
        File externalFile = new File(path);
        Uri uri = Uri.fromFile(externalFile);
        ContentResolver mconContentResolver = getContentResolver();
        InputStream in = null;
        try {


            in = mconContentResolver.openInputStream(uri);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();

            int scale = 1;
            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE) {
                scale++;
            }
            Log.d("TAG", "scale = " + scale + ", orig-width: " + o.outWidth
                    + ", orig-height: " + o.outHeight);

            Bitmap b = null;
            in = mconContentResolver.openInputStream(uri);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                o = new BitmapFactory.Options();
                o.inSampleSize = scale;
                b = BitmapFactory.decodeStream(in, null, o);

                // resize to desired dimensions
                int height = b.getHeight();
                int width = b.getWidth();
                Log.d("TAG", "1th scale operation dimenions - width: " + width
                        + ",height: " + height);

                double y = Math.sqrt(IMAGE_MAX_SIZE
                        / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
                        (int) y, true);
                b.recycle();
                b = scaledBitmap;


            } else {
                b = BitmapFactory.decodeStream(in);
            }
            in.close();

            Log.d("TAG", "bitmap size - width: " + b.getWidth() + ", height: "
                    + b.getHeight());
            SaveImage(b);
            return b;
        } catch (IOException e) {
            Log.e("TAG", e.getMessage(), e);
            return null;
        }
    }

    private void SaveImage(Bitmap finalBitmap) {

        File myDir = new File(android.os.Environment.getExternalStorageDirectory(), "/"+getString(R.string.app_name)+"/Profile Image");
        if(!myDir.exists())
            myDir.mkdirs();
        String fname = "IMG.jpg";
        file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void register() {
        M.showLoadingDialog(RegisterActivity.this);
        MultipartBody.Part body=null;
        if(file!=null) {
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
            // MultipartBody.Part is used to send also the actual file name
            body = MultipartBody.Part.createFormData("photo", file.getName(), requestFile);
        }
        RequestBody m = RequestBody.create(MediaType.parse("text/plain"), email);
        RequestBody n = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody g = RequestBody.create(MediaType.parse("text/plain"), gender);
        RequestBody l = RequestBody.create(MediaType.parse("text/plain"), login_type);
        RequestBody c = RequestBody.create(MediaType.parse("text/plain"), city);
        RequestBody p = RequestBody.create(MediaType.parse("text/plain"), pwd);
        RequestBody ph = RequestBody.create(MediaType.parse("text/plain"), phone);
        Log.d(TAG,n+" "+p+" "+m+" "+ph+" "+g+" "+c+" "+l+" "+body);
        APIAuthentication mAuthenticationAPI = Service.createService(RegisterActivity.this, APIAuthentication.class);
        Call<UserPojo> call = mAuthenticationAPI.register(n,p,m,ph,g,c,l,body);
        call.enqueue(new Callback<UserPojo>() {
            @Override
            public void onResponse(Call<UserPojo> call, Response<UserPojo> response) {
                Log.d("response:", "data:" + response);
                // response.isSuccessful() is true if the response code is 2xx
                if (response.isSuccessful()) {
                    UserPojo pojo = response.body();
                    if (pojo != null) {
                     //   Log.d(TAG, "res:" + pojo.getSuccess());
                        if(pojo.getSuccess()==1) {
                            if(pojo.getUser()!=null) {
                                User user=pojo.getUser();
                                M.setID(user.getId()+"", RegisterActivity.this);
                                M.setUsername(user.getName(), RegisterActivity.this);
                                M.setEmail(user.getEmail(), RegisterActivity.this);
                                M.setPhone(user.getMobile_no(), RegisterActivity.this);
                                M.setlogintype(user.getLogin_type(), RegisterActivity.this);
                                M.setPhoto(user.getPhoto(),RegisterActivity.this);
                                M.setRole("patient", RegisterActivity.this);
                                M.hideLoadingDialog();
                            }
                            M.hideLoadingDialog();
                            Toast.makeText(RegisterActivity.this,"Thank you for Registration.",Toast.LENGTH_SHORT).show();
                            Intent it = new Intent(RegisterActivity.this, SelectCity.class);
                            finish();
                            startActivity(it);

                        }else{
                            M.hideLoadingDialog();
                            Toast.makeText(RegisterActivity.this,"Email already exist...",Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        M.hideLoadingDialog();
                    }
                } else {
                    int statusCode = response.code();
                    ResponseBody errorBody = response.errorBody();
                    M.hideLoadingDialog();
                }
            }

            @Override
            public void onFailure(Call<UserPojo> call, Throwable t) {
                Log.d("response:", "fail:" + t.getMessage());
                Toast.makeText(RegisterActivity.this,"Fail.Try Again later...",Toast.LENGTH_SHORT).show();
                M.hideLoadingDialog();
                // handle execution failures like no internet connectivity
            }
        });
    }

    private boolean isValidEmaillId(String email){

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
                    mIntent = new Intent();
                    mIntent.setType("image/*");
                    mIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(mIntent, "Select Picture"),RESULT_LOAD_IMAGE1);
                    return true;
            } else {
                ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},2);
                return false;
            }
        }else {
            mIntent = new Intent();
            mIntent.setType("image/*");
            mIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(mIntent, "Select Picture"),RESULT_LOAD_IMAGE1);
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==2 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            mIntent = new Intent();
            mIntent.setType("image/*");
            mIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(mIntent, "Select Picture"),RESULT_LOAD_IMAGE1);
        }
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
