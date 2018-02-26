package com.doctor.app;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import com.doctor.app.Doctor.AppointmentFragment;
import com.doctor.app.Doctor.UpcomingFragment;
import com.doctor.app.adapter.DrawerAdapter;
import com.doctor.app.helper.AppConst;
import com.doctor.app.helper.ConnectionDetector;
import com.doctor.app.model.DrawerPojo;
import com.doctor.app.model.M;
import com.doctor.app.model.SuccessPojo;
import com.doctor.app.util.GPSTracker;
import com.doctor.app.view.CropSquareTransformation;
import com.doctor.app.webservice.APIAuthentication;
import com.doctor.app.webservice.DoctorAPI;
import com.doctor.app.webservice.Service;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<LocationSettingsResult> {

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    int fragmentposition = 1;
    String TAG="MainActivity",userid;
    Context context;
    ArrayList<DrawerPojo> list = new ArrayList<>();
    String role;
    protected GoogleApiClient mGoogleApiClient;
    private final int MY_PERMISSIONS_REQUEST_LOCATION = 3;
    int REQUEST_CHECK_SETTINGS = 100;
    protected LocationRequest locationRequest;
    ConnectionDetector connectionDetector;
    GPSTracker gpsTracker;
    SwitchCompat sw;
    TextView tvstatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=MainActivity.this;
        userid= M.getID(MainActivity.this);
        role=M.getRole(context);
        if(role==null)
            role="";
        isStoragePermissionGranted();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mTitle = mDrawerTitle = getTitle();
        mDrawerList = (ListView) findViewById(R.id.list_view);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);

        View headerView = getLayoutInflater().inflate(
                R.layout.header_navigation_drawer, mDrawerList, false);
        //add email,city in header
        ImageView iv = (ImageView) headerView.findViewById(R.id.image);
        TextView tvusername=(TextView)headerView.findViewById(R.id.tvusername);
        TextView tvemail=(TextView)headerView.findViewById(R.id.tvemail);
        TextView tvcity=(TextView)headerView.findViewById(R.id.tvcity);
        sw=(SwitchCompat)headerView.findViewById(R.id.sw);
        tvstatus=(TextView)headerView.findViewById(R.id.tvstatus);
        tvusername.setText(M.getUsername(MainActivity.this));
        tvemail.setText(M.getEmail(MainActivity.this));
        String imgurl;
        if(role.equals("doctor")){
            sw.setVisibility(View.VISIBLE);
            tvstatus.setVisibility(View.VISIBLE);
            tvcity.setVisibility(View.GONE);
            imgurl=AppConst.doctor_img_url;
        }else {
            tvcity.setVisibility(View.VISIBLE);
            sw.setVisibility(View.GONE);
            tvstatus.setVisibility(View.GONE);
            tvcity.setText(M.getCity(MainActivity.this));
            imgurl=AppConst.profile_img_url;
        }
        String pic=M.getPhoto(MainActivity.this);
        if(pic==null)
            pic="";

        if(pic.trim().length()>0){

            Picasso.with(MainActivity.this)
                    .load(imgurl + pic)
                    .transform(new CropSquareTransformation())
                    .placeholder(R.drawable.ic_user)
                    .error(R.drawable.ic_user)
                    .into(iv);
        }
        if(M.getStatus(context)!=null) {
            tvstatus.setTag(M.getStatus(context));
            if (M.getStatus(context).equals("1")) {
                sw.setChecked(true);
                tvstatus.setText(getString(R.string.txt_avaliable));
            } else {
                sw.setChecked(false);
                tvstatus.setText(getString(R.string.txt_unavaliable));
            }
        }
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    tvstatus.setText(getString(R.string.txt_avaliable));
                    tvstatus.setTag("1");
                    updateStatus("1");
                }else{
                    tvstatus.setText(getString(R.string.txt_unavaliable));
                    tvstatus.setTag("0");
                    updateStatus("0");
                }
            }
        });
        ImageView ivdrawer=(ImageView)findViewById(R.id.ivdrawer);
        ivdrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                }
                else {
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                }
            }
        });
        mDrawerList.addHeaderView(headerView);// Add header before adapter (for
        // pre-KitKat)
        mDrawerList.setAdapter(new DrawerAdapter(MainActivity.this,getDummyList()));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        int color = getResources().getColor(R.color.material_grey_100);
        color = Color.argb(0xCD, Color.red(color), Color.green(color),
                Color.blue(color));
        mDrawerList.setBackgroundColor(color);
        mDrawerList.getLayoutParams().width = (int) getResources()
                .getDimension(R.dimen.drawer_width);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer,
                R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if(getIntent().hasExtra("upcoming") && role.equals("doctor")){
            selectItem(1);
        }else if(getIntent().getAction()!=null){
            if(getIntent().getAction().equals("settings"))
                selectItem(list.size()-1);
            else if(getIntent().getAction().equals("appointment"))
                selectItem(1);
            else
                selectItem(0);
        }else{
            selectItem(0);
        }
        updateFcm();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public ArrayList<DrawerPojo> getDummyList() {
        list.clear();
        if(!role.equals("doctor"))
            list.add(new DrawerPojo(0, "",getString(R.string.item_home),R.drawable.ic_home));
        list.add(new DrawerPojo(1, "",getString(R.string.item_appointment),R.drawable.ic_appointment));
        if(role.equals("doctor"))
            list.add(new DrawerPojo(2, "",getString(R.string.item_upcoming),R.drawable.ic_appointment));
//        list.add(new DrawerPojo(3, "",getString(R.string.item_news),R.drawable.ic_news));
        if(!role.equals("doctor"))
            list.add(new DrawerPojo(3, "",getString(R.string.item_prescription),R.drawable.ic_prescription));
        list.add(new DrawerPojo(4, "",getString(R.string.item_notification),R.drawable.ic_notification));
        list.add(new DrawerPojo(5, "",getString(R.string.item_terms_conditionds),R.drawable.ic_terms));
        list.add(new DrawerPojo(6, "",getString(R.string.item_rating),R.drawable.ic_rating_us));
        list.add(new DrawerPojo(7, "",getString(R.string.item_share),R.drawable.ic_share));
        list.add(new DrawerPojo(8, "",getString(R.string.item_settings),R.drawable.ic_settings));
        return list;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!M.getRole(context).equals("doctor")) {
            menu.clear();
            getMenuInflater().inflate(R.menu.menu_home, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }else if(item.getItemId()==R.id.item_location){
            gpsTracker=new GPSTracker(context);
            checkGpsPermission();
        }
        return super.onOptionsItemSelected(item);
    }

    private class DrawerItemClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            if(position!=0) {
                selectItem(position-1);
            }
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }

    public void selectItem(int pos){
        Fragment fragment = null;
        fragmentposition=  pos;
        String item=list.get(pos).getText();
        if(item.equals(getString(R.string.item_home)))
            fragment=new SelectDepartment();
        else if(item.equals(getString(R.string.item_appointment))){
            if(role.equals("doctor"))
                fragment=new AppointmentFragment();
            else
                fragment=new Appointments();
        }else if(item.equals(getString(R.string.item_upcoming))){
            fragment=new UpcomingFragment();
        }else if(item.equals(getString(R.string.item_prescription))){
            fragment=new Prescription();
        }else if(item.equals(getString(R.string.item_notification))){
            fragment=new NotificationFragment();
        }else if(item.equals(getString(R.string.item_rating))){
            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }else if(item.equals(getString(R.string.item_share))){
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,AppConst.share);
            startActivity(Intent.createChooser(sharingIntent, "Share using"));
        }else if(item.equals(getString(R.string.item_terms_conditionds))){
            fragment=new TermsFragment();
        }else if(item.equals(getString(R.string.item_settings)))
            fragment=new Settings();


        if(fragment!=null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment);
            if(pos!=1)
                fragmentTransaction.addToBackStack(null);
            else{}
            fragmentTransaction.commit();
        }

        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(int titleId) {
        setTitle(getString(titleId));
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }else {
            if (getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        }
    }

    public void checkGpsPermission() {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.WRITE_CALENDAR},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        } else {
            checkGpsStatus();
        }
    }

    private void checkGpsStatus() {
        try {
            if (!gpsTracker.canGetLocation()) {
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this).build();
                mGoogleApiClient.connect();
                locationRequest = LocationRequest.create();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest.setInterval(30 * 1000);
                locationRequest.setFastestInterval(5 * 1000);
            } else {
                startHandler();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);
            builder.setAlwaysShow(true);
            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(
                            mGoogleApiClient,
                            builder.build()
                    );
            result.setResultCallback(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startHandler() {
        M.showLoadingDialog(MainActivity.this);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                M.hideLoadingDialog();
                connectionDetector=new ConnectionDetector(context);
                if(!connectionDetector.isConnectingToInternet())
                {
                    Toast.makeText(context,"No Internet conntecion",Toast.LENGTH_SHORT).show();
                }else {
                    Intent it=new Intent(context,MapsActivity.class);
                    startActivity(it);
                }
            }

        }, 1000);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG,"permi ok");
                startHandler();
            } else {
                Toast.makeText(MainActivity.this,"GPS not enabled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                //  Location settings are not satisfied. Show the user a dialog
                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    //failed to show
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                // Location settings are unavailable so not possible to show any dialog now
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkGpsStatus();
                }
                break;
        }
    }

    private void updateFcm(){
        String fcmid="";
        if(AppConst.fcm_id==null){
            fcmid= FirebaseInstanceId.getInstance().getToken();
            if(fcmid==null)
                fcmid="";
        }else if(AppConst.fcm_id.length()<=0){
            fcmid= FirebaseInstanceId.getInstance().getToken();
            if(fcmid==null)
                fcmid="";
        }else{
            fcmid=AppConst.fcm_id;
        }
        if(fcmid.length()>0) {
             //Log.d(TAG,"fcm:"+M.getID(MainActivity.this)+" "+M.getRole(context)+" "+fcmid);
            APIAuthentication mAuthenticationAPI = Service.createService(context, APIAuthentication.class);
            Call<SuccessPojo> call = mAuthenticationAPI.updateFcm(M.getID(context),M.getRole(context),fcmid);
            call.enqueue(new retrofit2.Callback<SuccessPojo>() {
                @Override
                public void onResponse(Call<SuccessPojo> call, Response<SuccessPojo> response) {
                    Log.d("response:", "data:" + response);
                    // response.isSuccessful() is true if the response code is 2xx
                    if (response.isSuccessful()) {
                        SuccessPojo pojo = response.body();
                        Log.d(TAG, "update fcm:" + pojo.getSuccess());
                    } else {
                        int statusCode = response.code();

                        // handle request errors yourself
                        ResponseBody errorBody = response.errorBody();
                        Log.d(TAG, "error:" + statusCode + " " + errorBody);
                    }
                }

                @Override
                public void onFailure(Call<SuccessPojo> call, Throwable t) {
                    Log.d("response:", "fail:" + t.getMessage());
                }
            });
        }
    }

    private void updateStatus(final String status) {

        M.showLoadingDialog(context);
        DoctorAPI mAuthenticationAPI = Service.createService(context,DoctorAPI.class);
        Call<SuccessPojo> call = mAuthenticationAPI.updateDrStaus(status,M.getID(context));
        call.enqueue(new retrofit2.Callback<SuccessPojo>() {
            @Override
            public void onResponse(Call<SuccessPojo> call, Response<SuccessPojo> response) {
                //Log.d("response:","data:"+response);
                if (response.isSuccessful()) {
                    SuccessPojo pojo = response.body();
                    if(pojo.getSuccess().equals("1")){
                        M.setStatus(status,context);
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
            public void onFailure(Call<SuccessPojo> call, Throwable t) {
                Log.d("response:","fail:"+t.getMessage());
                M.hideLoadingDialog();
                // handle execution failures like no internet connectivity
            }
        });
    }


    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
                return false;
            }
        } else {
            return true;
        }
    }
}
