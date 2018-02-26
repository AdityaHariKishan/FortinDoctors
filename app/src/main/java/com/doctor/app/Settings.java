package com.doctor.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doctor.app.Doctor.DoctorProfile;
import com.doctor.app.model.M;

public class Settings extends Fragment implements View.OnClickListener {

    View view;
    TextView tvlogout,tvcity,tvchangepwd,tveditprofile;
    SwitchCompat sw;
    LinearLayout llcity,llcalender;
    Context context;
    String TAG="Settings";

    public Settings() {
        // Required empty public constructor
    }

    public static Settings newInstance(String param1, String param2) {
        Settings fragment = new Settings();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_settings, container, false);
        context=getActivity();

        tvlogout=(TextView)view.findViewById(R.id.tvlogout);
        tvcity=(TextView)view.findViewById(R.id.tvcity);
        tvchangepwd=(TextView)view.findViewById(R.id.tvchangepwd);
        tveditprofile=(TextView)view.findViewById(R.id.tveditprofile);
        llcity=(LinearLayout)view.findViewById(R.id.llcity);
        llcalender=(LinearLayout)view.findViewById(R.id.llcalender);
        sw=(SwitchCompat)view.findViewById(R.id.sw);

        tvlogout.setOnClickListener(this);
        tvcity.setOnClickListener(this);
        tvchangepwd.setOnClickListener(this);
        tveditprofile.setOnClickListener(this);

        sw.setChecked(M.isCalenderpushON(context));

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                M.setCalenderpush(isChecked,context);
            }
        });

        if(M.getRole(context).equals("doctor")) {
            llcity.setVisibility(View.GONE);
            llcalender.setVisibility(View.GONE);
        }else{
            llcity.setVisibility(View.VISIBLE);
            llcalender.setVisibility(View.VISIBLE);
            tvcity.setText("City: "+M.getCity(context));
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        if(v==tvlogout){
            M.logOut(context);
            Intent it=new Intent(context,LoginActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            it.putExtra("EXIT", true);
            ((MainActivity)context).finish();
            startActivity(it);
        }else if(v==tvcity){
            Intent it=new Intent(context,SelectCity.class);
            it.setAction("settings");
            ((MainActivity)context).finish();
            startActivity(it);
        }else if(v==tvchangepwd){
            Intent it=new Intent(context,ChangePassword.class);
            startActivity(it);
        }else if(v==tveditprofile){
            if(M.getRole(context).equals("doctor")) {
                Intent it = new Intent(context, DoctorProfile.class);
                startActivity(it);
            }else {
                Intent it = new Intent(context, EditProfile.class);
                startActivity(it);
            }
        }
    }
}
