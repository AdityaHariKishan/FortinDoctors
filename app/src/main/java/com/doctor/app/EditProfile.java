package com.doctor.app;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.doctor.app.helper.AppConst;
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

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfile extends AppCompatActivity implements View.OnClickListener {

    ImageView ivprofile;
    Button btnsubmit;
    EditText etname,etphone;
    RadioGroup rg;
    RadioButton rbmale,rbfemale;

    Context context;
    String TAG="EditProfile",userid;
    Intent mIntent;
    int RESULT_LOAD_IMAGE1=1;

    String profile=null;
    String name="", phone="", gender="", login_type="email";

    File file=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context=EditProfile.this;
        userid= M.getID(context);
        initUI();
    }

    private void initUI() {
        etname=(EditText)findViewById(R.id.etusername);
        etphone=(EditText)findViewById(R.id.etphone);
        rg=(RadioGroup)findViewById(R.id.rg);
        rbfemale=(RadioButton)findViewById(R.id.rbfemale);
        rbmale=(RadioButton)findViewById(R.id.rbmale);
        ivprofile=(ImageView)findViewById(R.id.ivprofile);
        btnsubmit=(Button) findViewById(R.id.btnedit);
        
        ivprofile.setOnClickListener(this);
        btnsubmit.setOnClickListener(this);

        etname.setText(M.getUsername(context));
        if(M.getPhoto(context)!=null)
            etphone.setText(M.getPhone(context));

            rg.setVisibility(View.VISIBLE);
            if (M.getGender(context) != null) {
                gender = M.getGender(context);
                if (gender.equalsIgnoreCase("female"))
                    rbfemale.setChecked(true);
                else if (gender.equalsIgnoreCase("male"))
                    rbmale.setChecked(true);
            }

        String pic=M.getPhoto(context);
        if(pic==null)
            pic="";

        if(pic.trim().length()>0){
            Picasso.with(context)
                    .load(AppConst.profile_img_url + pic)
                    .transform(new CropSquareTransformation())
                    .placeholder(R.drawable.ic_user)
                    .error(R.drawable.ic_user)
                    .into(ivprofile);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.ivprofile){
            isStoragePermissionGranted();
        }else if(v.getId()==R.id.btnedit){
            if(etname.getText().toString().trim().length()<=0) {
                etname.setError("Username required");
            }else{
                name=etname.getText().toString();
                if(rbmale.isChecked())
                    gender="Male";
                else if(rbfemale.isChecked())
                    gender="Female";

                if(etphone.getText().toString().trim().length()>0)
                    phone=etphone.getText().toString();


                Log.d(TAG,"Name:"+name);
                Log.d(TAG,"profile:"+profile);
                Log.d(TAG,"phone:"+phone+" "+gender);
                Log.d(TAG,"file:"+file);
                updateProfile();
            }
        }else if(v.getId()==R.id.txtSignin){
            onBackPressed();
        }
    }

    public void updateProfile() {
        M.showLoadingDialog(EditProfile.this);
        MultipartBody.Part body=null;
        if(file!=null) {
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
            body = MultipartBody.Part.createFormData("photo", file.getName(), requestFile);
        }
        RequestBody id = RequestBody.create(MediaType.parse("text/plain"), M.getID(context));
        RequestBody n = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody g = RequestBody.create(MediaType.parse("text/plain"), gender);
        RequestBody ph = RequestBody.create(MediaType.parse("text/plain"), phone);
        Log.d(TAG,n+" "+ph+" "+g+" "+body);
        APIAuthentication mAuthenticationAPI = Service.createService(EditProfile.this, APIAuthentication.class);
        Call<UserPojo> call = mAuthenticationAPI.editProfile(id,n,ph,g,body);
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
                                M.setID(user.getId()+"", EditProfile.this);
                                M.setUsername(user.getName(), EditProfile.this);
                                M.setPhone(user.getMobile_no(), EditProfile.this);
                                M.setPhoto(user.getPhoto(),EditProfile.this);
                                M.hideLoadingDialog();
                            }
                            M.hideLoadingDialog();
                            Toast.makeText(EditProfile.this,"Profile updated Successfully...",Toast.LENGTH_SHORT).show();
                            Intent it = new Intent(EditProfile.this, MainActivity.class);
                            finish();
                            startActivity(it);

                        }else{
                            M.hideLoadingDialog();
                            Toast.makeText(EditProfile.this,"Edit profile failed...",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(EditProfile.this,"Fail.Try Again later...",Toast.LENGTH_SHORT).show();
                M.hideLoadingDialog();
                // handle execution failures like no internet connectivity
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == this.RESULT_OK && requestCode == RESULT_LOAD_IMAGE1) {
            profile = FilePath.getPath(EditProfile.this, data.getData());

            Picasso.with(EditProfile.this)
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
