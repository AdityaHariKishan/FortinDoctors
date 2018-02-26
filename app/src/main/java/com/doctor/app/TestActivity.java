package com.doctor.app;

import android.*;
import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class TestActivity extends AppCompatActivity {

    String TAG="TestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Button btn=(Button)findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment="Test cal";
                SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
                Calendar dt = Calendar.getInstance();

// Where untilDate is a date instance of your choice, for example 30/01/2012
//                try {
//                    dt.setTime(dtfmt.parse(appointment_date));
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }

// If you want the event until 30/01/2012, you must add one day from our day because UNTIL in RRule sets events before the last day
                dt.add(Calendar.DATE, 1);
                String dtUntill = yyyyMMdd.format(dt.getTime());
                ContentResolver cr = getContentResolver();
                ContentValues values = new ContentValues();

                values.put(CalendarContract.Events.DTSTART,dt.getTimeInMillis());
                values.put(CalendarContract.Events.TITLE, "appointment");
                values.put(CalendarContract.Events.DESCRIPTION, comment);

                TimeZone timeZone = TimeZone.getDefault();
                values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());

// Default calendar
                values.put(CalendarContract.Events.CALENDAR_ID, 1);

                values.put(CalendarContract.Events.RRULE, "FREQ=DAILY;UNTIL="+ dtUntill);
// Set Period for 1 Hour
                values.put(CalendarContract.Events.DURATION, "+P1H");

                values.put(CalendarContract.Events.HAS_ALARM, 1);

                if (ActivityCompat.checkSelfPermission(TestActivity.this, android.Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                    //return;
                }else {
                    Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
                    Log.d(TAG,"uri:"+uri);
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(uri);
                    startActivity(i);
                }
            }
        });
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_CALENDAR)== PackageManager.PERMISSION_GRANTED) {

                return true;
            } else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_CALENDAR},2);
                return false;
            }
        }else {
            return true;
        }
    }
}
