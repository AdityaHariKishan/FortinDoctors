package com.doctor.app;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.doctor.app.helper.AppConst;
import com.doctor.app.model.M;
import com.doctor.app.model.NotificationPojo;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    SimpleDateFormat dtfmt=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Uri uri;
    String appointment_status="";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if(M.getID(this)!=null) {
            if (remoteMessage.getData() != null) {
                String id = remoteMessage.getData().get("id");
                String department_id = remoteMessage.getData().get("department_id");
                String doctor_id = remoteMessage.getData().get("doctor_id");
                String appointment_date = remoteMessage.getData().get("appointment_date");
                String appointment_time = remoteMessage.getData().get("appointment_time");
                Log.d(TAG, "appointment date:" + appointment_date + " " + appointment_time);
                String patient_id = remoteMessage.getData().get("patient_id");
                String note = remoteMessage.getData().get("note");
                appointment_status = remoteMessage.getData().get("appointment_status");
                String created_time = remoteMessage.getData().get("created_time");
                String doctor_name = remoteMessage.getData().get("doctor_name");
                String doctor_image = remoteMessage.getData().get("doctor_image");
                String patient_name = remoteMessage.getData().get("patient_name");
                String patient_photo = remoteMessage.getData().get("patient_photo");
                String patient_email = remoteMessage.getData().get("patient_email");
                NotificationPojo n = new NotificationPojo();
                n.setId(id);
                n.setDepartment_id(department_id);
                n.setDoctor_id(doctor_id);
                n.setAppointment_date(appointment_date);
                n.setAppointment_time(appointment_time);
                n.setPatient_id(patient_id);
                n.setNote(note);
                n.setAppointment_status(appointment_status);
                n.setCreated_time(created_time);
                n.setDoctor_name(doctor_name);
                n.setDoctor_image(doctor_image);
                n.setPatient_name(patient_name);
                n.setPatient_photo(patient_photo);
                n.setPatient_email(patient_email);
                AppConst.selNotification = n;
                if (M.getRole(this)!=null && !M.getRole(this).equals("doctor") && appointment_status.equals(AppConst.status_accept) && M.isCalenderpushON(this)) {
                    String comment = "At " + appointment_date + " " + appointment_time;
                    SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
                    Calendar dt = Calendar.getInstance();

// Where untilDate is a date instance of your choice, for example 30/01/2012
                    try {
                        dt.setTime(dtfmt.parse(appointment_date + " " + appointment_time));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

// If you want the event until 30/01/2012, you must add one day from our day because UNTIL in RRule sets events before the last day
                    // dt.add(Calendar.DATE, 1);
                    String dtUntill = yyyyMMdd.format(dt.getTime());
                    ContentResolver cr = getContentResolver();
                    ContentValues values = new ContentValues();

                    values.put(CalendarContract.Events.DTSTART, dt.getTimeInMillis());
                    values.put(CalendarContract.Events.TITLE, "Your appointment with " + doctor_name);
                    values.put(CalendarContract.Events.DESCRIPTION, comment);

                    TimeZone timeZone = TimeZone.getDefault();
                    values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());

// Default calendar
                    values.put(CalendarContract.Events.CALENDAR_ID, 1);

                    values.put(CalendarContract.Events.RRULE, "FREQ=DAILY;UNTIL=" + dtUntill);
// Set Period for 1 Hour
                    values.put(CalendarContract.Events.DURATION, "+P1H");

                    values.put(CalendarContract.Events.HAS_ALARM, 1);

                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                        //return;
                    } else {
                        uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
                    //    Log.d(TAG, "uri:" + uri);
//                        Intent i = new Intent(Intent.ACTION_VIEW);
//                        i.setData(uri);
//                        startActivity(i);
                    }
                }
            }
            sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(),uri,appointment_status);
        }
    }

    private void sendNotification(String title, String messageBody, Uri uri,String appointment_status) {
        Intent intent;
        if(M.getRole(this).equals("doctor")) {
            intent = new Intent(this, MainActivity.class);
            intent.putExtra("upcoming","true");
        }else if (M.getRole(this)!=null && !M.getRole(this).equals("doctor") && appointment_status.equals(AppConst.status_accept) && M.isCalenderpushON(this)) {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
        }else if(!M.getRole(this).equals("doctor"))
            intent= new Intent(this, WriteReview.class);
        else
            intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}