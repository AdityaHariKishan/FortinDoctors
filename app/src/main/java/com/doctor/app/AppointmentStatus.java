package com.doctor.app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class AppointmentStatus extends AppCompatActivity implements View.OnClickListener {

    TextView txt;
    Button btnhome;

    Context context;
    String TAG="AppointmentStatus";
    String dt,tm;
    SimpleDateFormat dfmt=new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat tfmt=new SimpleDateFormat("HH:mm:ss");
    SimpleDateFormat dtfmt=new SimpleDateFormat("dd MMM yyyy");
    SimpleDateFormat tmfmt=new SimpleDateFormat("HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_status);
        context=AppointmentStatus.this;
        if(getIntent().getExtras()!=null){
            dt=getIntent().getStringExtra("date");
            tm=getIntent().getStringExtra("time");
        }else{
            finish();
        }
        initview();

    }

    private void initview() {

        txt=(TextView)findViewById(R.id.txt1);
        btnhome=(Button)findViewById(R.id.btnhome);
        String d="",t="";
        try {
            d=dtfmt.format(dfmt.parse(dt));
            t=tmfmt.format(tfmt.parse(tm));
            txt.setText("Thank you for booked appointment.\nYour appointment is booked at\n"+d+" "+t);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        btnhome.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==btnhome)
            onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent it=new Intent(context,MainActivity.class);
        finish();
        startActivity(it);
    }
}
