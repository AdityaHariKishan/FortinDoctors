package com.doctor.app.Doctor;

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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;
import com.doctor.app.R;
import com.doctor.app.adapter.RecyclerListAdapter;
import com.doctor.app.helper.AppConst;
import com.doctor.app.helper.FilePath;
import com.doctor.app.model.CityPojo;
import com.doctor.app.model.Clinic_image;
import com.doctor.app.model.DepartmentPojo;
import com.doctor.app.model.DoctorDetailPojo;
import com.doctor.app.model.M;
import com.doctor.app.model.SuccessPojo;
import com.doctor.app.util.GSSettings;
import com.doctor.app.util.IOHelper;
import com.doctor.app.view.CropSquareTransformation;
import com.doctor.app.view.MultiSelectionSpinner;
import com.doctor.app.webservice.APIAuthentication;
import com.doctor.app.webservice.DoctorAPI;
import com.doctor.app.webservice.Service;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoctorProfile extends AppCompatActivity implements View.OnClickListener {

    EditText etname,etperiod,etaddress,etclinic_name,etclinic_phone,etphone,etabout,eteducation;
    MultiSelectionSpinner spndepart;
    Spinner spncity;
    ImageView ivprofile,btnadd;
    Button btnsubmit;
    SwitchCompat sw;
    TextView tvstatus;
    RecyclerView rvfiles;

    String TAG="DoctorProfile";
    Context context;
    DoctorDetailPojo pojo;
    String doctor_id,doctor_name,period,status,city,address,clinic_name,clinic_phone,phone,about,img,education;
    Intent mIntent;
    int RESULT_LOAD_IMAGE1=1;
    File file=null;
    String profile=null,dep;
    ArrayList<String> cities=new ArrayList<>();
    ArrayList<DepartmentPojo> departments=new ArrayList<>();
    ArrayList<Clinic_image> clinic_images=new ArrayList<>();
    ArrayList<String> dlist=new ArrayList<>();
    RecyclerListAdapter adapter;
    private SavedState mSS;
    private DisplayMetrics mDisplayMetrics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context=DoctorProfile.this;
        doctor_id=M.getID(context);
        GSSettings.InitializeSettings(context);
        final SavedState ss = (SavedState)getLastNonConfigurationInstance();
//        if(ss != null)
//            this.mSS = ss;
//        else{
//            this.mSS = new SavedState();
//            IOHelper.clearTmpDirectory();
//        }
        IOHelper.clearTmpDirectory();
        this.mDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(this.mDisplayMetrics);
        initview();
    }

    private void initview() {

        etname=(EditText)findViewById(R.id.etusername);
        etphone=(EditText)findViewById(R.id.etphone);
        etperiod=(EditText)findViewById(R.id.etproid);
        etaddress=(EditText)findViewById(R.id.etaddress);
        etclinic_name=(EditText)findViewById(R.id.etclinicname);
        etclinic_phone=(EditText)findViewById(R.id.etclinicphone);
        etabout=(EditText)findViewById(R.id.etabout);
        eteducation=(EditText)findViewById(R.id.eteducation);

        spndepart=(MultiSelectionSpinner)findViewById(R.id.spndepartment);
        spncity=(Spinner)findViewById(R.id.spncity);
        ivprofile=(ImageView)findViewById(R.id.ivprofile);
        btnsubmit=(Button) findViewById(R.id.btnedit);
        sw=(SwitchCompat)findViewById(R.id.sw);
        tvstatus=(TextView)findViewById(R.id.tvstatus);
        btnadd=(ImageView)findViewById(R.id.btnadd);
        rvfiles = (RecyclerView)findViewById(R.id.rvfiles);
        rvfiles.setHasFixedSize(true);
        rvfiles.setNestedScrollingEnabled(true);
        adapter = new RecyclerListAdapter(context,clinic_images);
        ivprofile.setOnClickListener(this);
        btnsubmit.setOnClickListener(this);
        btnadd.setOnClickListener(this);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    tvstatus.setText(getString(R.string.txt_avaliable));
                    tvstatus.setTag("1");
                }else{
                    tvstatus.setText(getString(R.string.txt_unavaliable));
                    tvstatus.setTag("0");
                }
            }
        });
        getDoctorDetail();

    }

    private void getDoctorDetail() {

        M.showLoadingDialog(context);
        APIAuthentication mAuthenticationAPI = Service.createService(context,APIAuthentication.class);
        Call<DoctorDetailPojo> call = mAuthenticationAPI.getDoctorDetail(M.getID(context));
        call.enqueue(new retrofit2.Callback<DoctorDetailPojo>() {
            @Override
            public void onResponse(Call<DoctorDetailPojo> call, Response<DoctorDetailPojo> response) {
                //Log.d("response:","data:"+response);
                if (response.isSuccessful()) {
                    pojo = response.body();
                    if(pojo!=null){
                        clinic_name=pojo.getClinic_name();
                        doctor_name=pojo.getDoctor_name();
                        address=pojo.getAddress();
                        clinic_phone=pojo.getClinic_phone();
                        phone=pojo.getPersonal_phone();
                        about=pojo.getAbout();
                        img=pojo.getDoctor_image();
                        city=pojo.getCity();
                        education=pojo.getEducation_details();
                        period=pojo.getPeriod();
                        status=pojo.getAvailbility_status();
                        etclinic_name.setText(clinic_name);
                        etname.setText(doctor_name);
                        Picasso.with(context)
                                .load(AppConst.doctor_img_url+img)
                                .transform(new CropSquareTransformation())
                                .placeholder(R.drawable.ic_user)
                                .error(R.drawable.ic_user)
                                .into(ivprofile);
                        etaddress.setText(address);
                        etphone.setText(phone);
                        etclinic_phone.setText(clinic_phone);
                        etabout.setText(about);
                        eteducation.setText(education);
                        etperiod.setText(period);
                        tvstatus.setTag(status);
                        if(status.equals("1")) {
                            sw.setChecked(true);
                            tvstatus.setText(getString(R.string.txt_avaliable));
                        }else{
                            sw.setChecked(false);
                            tvstatus.setText(getString(R.string.txt_unavaliable));
                        }
                        for(DepartmentPojo dp:pojo.getDepartment()){
                            dlist.add(dp.getName());
                        }

                        if(pojo.getClinic_images().size()>0){
                            for(Clinic_image cimg:pojo.getClinic_images()) {
                                Clinic_image c=new Clinic_image();
                                c.setDoctor_id(cimg.getDoctor_id());
                                c.setId(cimg.getId());
                                c.setImage_name(cimg.getImage_name());
                                c.setIsserver(true);
                               adapter.onAdd(c);
                            }
                            adapter.notifyDataSetChanged();
                            handleAddFrame();
                        }
                    }
                    M.hideLoadingDialog();
                    getCity();
                } else {
                    int statusCode = response.code();
                    M.hideLoadingDialog();
                    // handle request errors yourself
                    ResponseBody errorBody = response.errorBody();
                }
            }

            @Override
            public void onFailure(Call<DoctorDetailPojo> call, Throwable t) {
                Log.d("response:","fail:"+t.getMessage());
                M.hideLoadingDialog();
                // handle execution failures like no internet connectivity
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.ivprofile){
            isStoragePermissionGranted();
        }else if(v.getId()==R.id.btnadd){
            showChooseImageSourceAlert();
        }else if(v.getId()==R.id.txtSignin){
            onBackPressed();
        }else if(v==btnsubmit){
            if(etname.getText().toString().trim().length()<=0){
                etname.setError("Name Required");
            }else if(spndepart.getSelectedIndicies().size()<=0){
                Toast.makeText(context,"Select minimum one department",Toast.LENGTH_SHORT).show();
            }else if(etperiod.getText().toString().trim().length()<=0){
                etperiod.setError("Period Required");
            }else if(etaddress.getText().toString().trim().length()<=0){
                etaddress.setError("Address Required");
            }else if(etclinic_name.getText().toString().trim().length()<=0){
                etclinic_name.setError("Clinic Name Required");
            }else if(etclinic_phone.getText().toString().trim().length()<=0){
                etclinic_phone.setError("Clinic Phone Required");
            }else if(eteducation.getText().toString().trim().length()<=0){
                eteducation.setError("Education Required");
            }else{
                doctor_name=etname.getText().toString();
                dep="";
                for(Integer d:spndepart.getSelectedIndicies()){
                    if(dep.trim().length()<=0)
                        dep=departments.get(d).getId();
                    else
                        dep=dep+","+departments.get(d).getId();
                }
                period=etperiod.getText().toString();
                status=tvstatus.getTag().toString();
                city=spncity.getSelectedItem().toString();
                address=etaddress.getText().toString();
                clinic_name=etclinic_name.getText().toString();
                clinic_phone=etclinic_phone.getText().toString();
                phone=etphone.getText().toString();
                about=etabout.getText().toString();
                education=eteducation.getText().toString();
//                Log.d(TAG,"doctor id:"+doctor_id);
//                Log.d(TAG,"doctor name:"+doctor_name+" "+phone);
//                Log.d(TAG,"department:"+dep);
//                Log.d(TAG,"period:"+period);
//                Log.d(TAG,"status:"+status);
//                Log.d(TAG,"city"+city);
//                Log.d(TAG,"address"+address);
//                Log.d(TAG,"cnm"+clinic_name+" "+clinic_phone);
//                Log.d(TAG,"about:"+about);


                uploadimages();
            }
        }
    }
    
    private void uploadimages(){
        M.showLoadingDialog(context);
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("doctor_id", doctor_id);
        builder.addFormDataPart("doctor_name", doctor_name);
        builder.addFormDataPart("department_id", dep);
        builder.addFormDataPart("period", period);
        builder.addFormDataPart("availbility_status", status);
        builder.addFormDataPart("city", city);
        builder.addFormDataPart("address", address);
        builder.addFormDataPart("clinic_name", clinic_name);
        builder.addFormDataPart("clinic_phone", clinic_phone);
        builder.addFormDataPart("about", about);
        builder.addFormDataPart("personal_phone", phone);
        builder.addFormDataPart("education_details", education);
        if (file != null) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), file);
            builder.addFormDataPart("doctor_image", file.getName(), requestBody);
        }

        ArrayList<Clinic_image> filepath=adapter.getItems();
        for (int i = 0, size = filepath.size(); i < size; i++) {
            if(!filepath.get(i).getIsserver()) {
                if(filepath.get(i).getImage_name()!=null) {
                    File f = new File(filepath.get(i).getImage_name());
                    RequestBody requestBody1 = RequestBody.create(MediaType.parse("image/jpeg"), f);
                    builder.addFormDataPart("clinic_images[]", f.getName(), requestBody1);
                }
            }
        }
        RequestBody finalRequestBody = builder.build();
        DoctorAPI mAuthenticationAPI = Service.createService(DoctorProfile.this, DoctorAPI.class);
        Call<SuccessPojo> response = mAuthenticationAPI.editProfile(finalRequestBody);

        response.enqueue(new Callback<SuccessPojo>() {
            @Override
            public void onResponse(Call<SuccessPojo> call, Response<SuccessPojo> response) {
                if (response.isSuccessful()) {
                    SuccessPojo pojo = response.body();
                    M.hideLoadingDialog();
                    if (pojo != null) {
                        if(pojo.getSuccess().equals("1"))
                            Toast.makeText(DoctorProfile.this,"Profile updated Successfully...",Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(DoctorProfile.this,"Edit profile failed...",Toast.LENGTH_SHORT).show();
                    } else {

                    }
                } else {
                    int statusCode = response.code();
                    ResponseBody errorBody = response.errorBody();
                    M.hideLoadingDialog();
                }
            }

            @Override
            public void onFailure(Call<SuccessPojo> call, Throwable t) {
                M.hideLoadingDialog();
                Log.e("Failure", "Failure"+t.getMessage());
            }
        });
    }

    private void showChooseImageSourceAlert(){
        if(adapter.getItemCount() >=20){
            Toast.makeText(context,"Sorry,You can't attach more than 20 photos.",Toast.LENGTH_SHORT).show();
            //DialogHelper.getErrorDialog(getActivity(), null, "Sorry,You can't attach more than 5 photos.").show();
        }else{
            int limit=20-adapter.getItemCount();
            Intent intent = new Intent(context, AlbumSelectActivity.class);
            intent.putExtra(Constants.INTENT_EXTRA_LIMIT,limit);
            startActivityForResult(intent, Constants.REQUEST_CODE);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == this.RESULT_OK && requestCode == RESULT_LOAD_IMAGE1) {
            profile = FilePath.getPath(context, data.getData());

            Picasso.with(context)
                    .load(data.getData())
                    .transform(new CropSquareTransformation())
                    .placeholder(R.drawable.ic_user)
                    .error(R.drawable.ic_user)
                    .into(ivprofile);
            Bitmap imgbitmap=getBitmap(profile);
        }else  if (requestCode == Constants.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            //The array list has the image paths of the selected images
            ArrayList<Image> images = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
            for (int i = 0, l = images.size(); i < l; i++) {
                //  namelist.add(images.get(i).name);
                Clinic_image c=new Clinic_image();
                c.setDoctor_id(M.getID(context));
                c.setId("0");
                c.setImage_name(images.get(i).path);
                c.setIsserver(false);
                adapter.onAdd(c);
                adapter.notifyDataSetChanged();
            }
            this.handleAddFrame();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home);
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void handleAddFrame(){

        rvfiles.setAdapter(adapter);
        LinearLayoutManager layoutManager= new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rvfiles.setLayoutManager(layoutManager);
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
                        int i=0,selpos=0;
                        for(CityPojo data:pojo){
                            cities.add(data.getCity());
                            if(data.equals(city))
                                selpos=i;
                            i++;
                        }
                        if(cities.size()>0){
                            ArrayAdapter<String> ada=new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,cities);
                            spncity.setAdapter(ada);
                            spncity.setSelection(selpos);
                        }
                    }
                    M.hideLoadingDialog();
                    getDepartments();
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

    private void getDepartments() {
        M.showLoadingDialog(context);
        APIAuthentication mAuthenticationAPI = Service.createService(context,APIAuthentication.class);
        Call<List<DepartmentPojo>> call = mAuthenticationAPI.getDepartmentByCity("");
        call.enqueue(new retrofit2.Callback<List<DepartmentPojo>>() {
            @Override
            public void onResponse(Call<List<DepartmentPojo>> call, Response<List<DepartmentPojo>> response) {
                Log.d("response:","data:"+response);
                if (response.isSuccessful()) {
                    List<DepartmentPojo> pojo = response.body();
                    ArrayList<String> nmlist=new ArrayList<String>();
                    if(pojo.size()>0){
                        departments.clear();
                        nmlist.clear();
                        departments= (ArrayList<DepartmentPojo>) pojo;
                        for(DepartmentPojo data:pojo){
                            nmlist.add(data.getName());
                        }
                        if(departments.size()>0){
                           // ArrayAdapter<String> ada=new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,nmlist);
                           // spndepart.setAdapter(ada);
                            spndepart.setItems(nmlist);
                            spndepart.setSelection(dlist);
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

    private class SavedState extends Object{
        public ArrayList<String> currentFrames = new ArrayList<String>();
        public boolean inSettings = false;
        public boolean inImageSourceSelect = false;
        public boolean inConfirmDelete = false;
        public boolean inConfirmClear = false;
        public boolean inConfirmExit = false;
        public boolean inFrameOptions = false;
        public boolean isViewingFrame = false;
        private int mFocusPosition = -1;
    }
}
