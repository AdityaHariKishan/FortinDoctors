package com.doctor.app;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.doctor.app.helper.AppConst;
import com.doctor.app.model.AppointmentPojo;
import com.doctor.app.model.DoctorPojo;
import com.doctor.app.model.DoctorPojo;
import com.doctor.app.model.M;
import com.doctor.app.util.GPSTracker;
import com.doctor.app.view.CropSquareTransformation;
import com.doctor.app.webservice.APIAuthentication;
import com.doctor.app.webservice.Service;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Context context;
    String TAG="MapsActivity";
    GPSTracker gps;
    Double lat,lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context=MapsActivity.this;
        gps=new GPSTracker(context);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if(gps.getLocation()!=null){
            lat=gps.getLatitude();
            lng=gps.getLongitude();
        }else{
            lat=0.0;
            lng=0.0;
        }
        getHospitals();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(final Marker arg0) {
                arg0.showInfoWindow();
                return true;
            }
        });
        LatLng sydney = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Me"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney,14));

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if(!marker.getTitle().equals("Me")) {
                    Intent it = new Intent(context, DoctorDetail.class);
                    it.putExtra("doctorid", marker.getTitle().split("-")[3]);
                    startActivity(it);
                }
            }
        });
    }

    private void getHospitals(){
        M.showLoadingDialog(context);
        APIAuthentication mAuthenticationAPI = Service.createService(context,APIAuthentication.class);
        Call<List<DoctorPojo>> call = mAuthenticationAPI.getHospitals(lat+"",lng+"", AppConst.distance+"");
        call.enqueue(new Callback<List<DoctorPojo>>() {
            @Override
            public void onResponse(Call<List<DoctorPojo>> call, Response<List<DoctorPojo>> response) {
                Log.d("response:","data:"+response);
                if (response.isSuccessful()) {
                    List<DoctorPojo> pojo=response.body();
                    if(pojo!=null){
                        if(pojo.size()>0) {
                        for(DoctorPojo data:pojo) {
                            if(data.getLatitude()!=null && data.getLatitude().trim().length()>0 && data.getLongitude()!=null && data.getLongitude().trim().length()>0) {
                                Double mlat= Double.valueOf(data.getLatitude());
                                Double mlng= Double.valueOf(data.getLongitude());
                                if(mlat>0 && mlng>0)
                                    mMap.addMarker(new MarkerOptions().position(new LatLng(mlat,lng)).title(data.getDoctor_name()+"-"+data.getDoctor_image()+"-"+data.getDistance()+"-"+data.getId()+"-"+data.getClinic_name())
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)));;
                            }
                        }
                            if (AppConst.hospitalPojoArrayList == null)
                                AppConst.hospitalPojoArrayList = new ArrayList<DoctorPojo>();
                            AppConst.hospitalPojoArrayList.clear();
                            AppConst.hospitalPojoArrayList.addAll(pojo);
                        }
                    }else{

                    }

                } else {
                    int statusCode = response.code();
                    ResponseBody errorBody = response.errorBody();
                    Log.d(TAG,"error:"+statusCode+" "+errorBody);
                }
                M.hideLoadingDialog();
            }

            @Override
            public void onFailure(Call<List<DoctorPojo>> call, Throwable t) {
                Log.d("response:","fail:"+t.getMessage());
                M.hideLoadingDialog();
            }
        });
    }

    public class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private View myContentsView;

        public MyInfoWindowAdapter() {
            myContentsView = getLayoutInflater().inflate(
                    R.layout.custom_info_window, null);
        }

        @Override
        public View getInfoContents(Marker marker) {

            if (marker != null
                    && marker.isInfoWindowShown()) {
                marker.hideInfoWindow();
                marker.showInfoWindow();
            }
            return null;
        }

        @Override
        public View getInfoWindow(final Marker marker) {
            TextView tvName = (TextView) myContentsView.findViewById(R.id.tvusername);
            TextView tvclinic = (TextView) myContentsView.findViewById(R.id.tvclinic);
            TextView tvdistance = (TextView) myContentsView.findViewById(R.id.tvdistance);
            TextView tvaddress = (TextView) myContentsView.findViewById(R.id.tvaddress);
            ImageView ivprofile = (ImageView) myContentsView.findViewById(R.id.ivprofile);
            LinearLayout llmap=(LinearLayout)myContentsView.findViewById(R.id.llmap);
            if(marker.getTitle().equals("Me")){
                tvName.setText(M.getUsername(context));
                tvdistance.setVisibility(View.GONE);
                tvclinic.setVisibility(View.GONE);
                tvaddress.setVisibility(View.GONE);
                Picasso.with(context)
                        .load(AppConst.profile_img_url + M.getPhoto(context))
                        .transform(new CropSquareTransformation())
                        .placeholder(R.drawable.ic_user)
                        .error(R.drawable.ic_user)
                        .into(ivprofile);
            }else {
                tvName.setText(marker.getTitle().split("-")[0]);
                String dist = String.format("%.2f", Float.parseFloat(marker.getTitle().split("-")[2]));
                tvdistance.setText("Distance: " + dist + " km");
                tvclinic.setText(marker.getTitle().split("-")[4]);
                tvaddress.setVisibility(View.GONE);
                tvdistance.setVisibility(View.VISIBLE);
                tvclinic.setVisibility(View.VISIBLE);
                String url = marker.getTitle().split("-")[1];
                if (url != null && !url.equalsIgnoreCase("null") && !url.equalsIgnoreCase("")) {
                    Picasso.with(context)
                            .load(AppConst.doctor_img_url + url)
                            .transform(new CropSquareTransformation())
                            .placeholder(R.drawable.ic_user)
                            .error(R.drawable.ic_user)
                            .into(ivprofile);
                } else {
                    ivprofile.setImageResource(R.drawable.ic_user);
                }
            }
            return myContentsView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_maps,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.item_list){
            Intent it=new Intent(context,NearByHospital.class);
            startActivity(it);
        }else if(item.getItemId()==android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
