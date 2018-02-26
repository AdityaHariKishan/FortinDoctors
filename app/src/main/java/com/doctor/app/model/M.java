package com.doctor.app.model;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.doctor.app.R;
import com.doctor.app.helper.AppConst;


public class M {
    static ProgressDialog pDialog;
    private static SharedPreferences mSharedPreferences;

    public static void showLoadingDialog(Context mContext) {
        pDialog = new ProgressDialog(mContext);
        pDialog.setMessage(mContext.getString(R.string.please_wait));
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    public static void hideLoadingDialog() {

        if(pDialog!=null)
            pDialog.dismiss();

    }


    public static void cancelableshowLoadingDialog(Context mContext) {
        pDialog = new ProgressDialog(mContext);
        pDialog.setMessage(mContext.getString(R.string.please_wait));
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    public static void hidecancelLoadingDialog() {

        if(pDialog!=null)
            pDialog.dismiss();

    }

    public static void showToast(Context mContext, String message) {

      Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();

    }

    public static void T(Context mContext, String Message) {
        Toast.makeText(mContext, Message, Toast.LENGTH_SHORT).show();
    }

    public static void L(String Message) {
        Log.e(AppConst.TAG, Message);
    }

    public static boolean setCalenderpush(boolean ispushON, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("ispushON", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean("ispushON", ispushON);
        return editor.commit();
    }

    public static boolean isCalenderpushON(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("ispushON", 0);
        return mSharedPreferences.getBoolean("ispushON", true);
    }

    public static boolean setUsername(String username, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("username", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("username", username);
        return editor.commit();
    }

    public static String getUsername(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("username", 0);
        return mSharedPreferences.getString("username", null);
    }


    public static boolean setEmail(String email, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("email", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("email", email);
        return editor.commit();
    }

    public static String getEmail(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("email", 0);
        return mSharedPreferences.getString("email", null);
    }


    public static boolean setPhone(String phone, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("phone", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("phone", phone);
        return editor.commit();
    }

    public static String getPhone(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("phone", 0);
        return mSharedPreferences.getString("phone", null);
    }

    public static boolean setCity(String city, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("city", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("city", city);
        return editor.commit();
    }

    public static String getCity(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("city", 0);
        return mSharedPreferences.getString("city", null);
    }

    public static boolean setDepartment(String department, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("department", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("department", department);
        return editor.commit();
    }

    public static String getDepartment(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("department", 0);
        return mSharedPreferences.getString("department", null);
    }

    public static boolean setPhoto(String photo, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("photo", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("photo", photo);
        return editor.commit();
    }

    public static String getPhoto(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("photo", 0);
        return mSharedPreferences.getString("photo", null);
    }

    public static boolean setGender(String gender, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("gender", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("gender", gender);
        return editor.commit();
    }

    public static String getGender(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("gender", 0);
        return mSharedPreferences.getString("gender", null);
    }

    public static boolean setlogintype(String logintype, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("logintype", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("logintype", logintype);
        return editor.commit();
    }

    public static String getlogintype(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("logintype", 0);
        return mSharedPreferences.getString("logintype", null);
    }

    public static boolean setRole(String role, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("role", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("role", role);
        return editor.commit();
    }

    public static String getRole(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("role", 0);
        return mSharedPreferences.getString("role", null);
    }

    public static boolean setStatus(String status, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("status", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("status", status);
        return editor.commit();
    }

    public static String getStatus(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("status", 0);
        return mSharedPreferences.getString("status", null);
    }

    public static void logOut(Context mContext) {
        setID("", mContext);
        setUsername(null, mContext);
        setPhone(null,mContext);
        setEmail(null,mContext);
        setCity(null,mContext);
        setDepartment(null,mContext);
        setGender(null,mContext);
        setlogintype(null,mContext);
        setPhoto(null,mContext);
        setRole(null,mContext);
        setStatus(null,mContext);
        mSharedPreferences.getAll().clear();
    }

    public static boolean setID(String ID, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        editor.putString("userid", ID);
        return editor.commit();
    }

    public static String getID(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        return mSharedPreferences.getString("userid", "");
    }

    public static String loadSavedPreferences(String etvalue, Context ctx) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
        String value=mSharedPreferences.getString(etvalue, "0");
        return value;
    }


    //Save String Value
    public static void savePreferences(String key, String value, Context ctx) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putString(key, value);
        edit.commit();
    }


    public static boolean isValidUrl(String text) {
        return Patterns.WEB_URL.matcher(text).matches();
    }


//    public static void showNotification(Context mContext, Intent resultIntent, String title, String text, int id) {
//        PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext, 0,
//                resultIntent, PendingIntent.FLAG_ONE_SHOT);
//
//        if(M.getUsername(mContext) != null) {
//            NotificationCompat.Builder mNotifyBuilder;
//            NotificationManager mNotificationManager;
//
//            mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
//            long when = System.currentTimeMillis();
//            mNotifyBuilder = new NotificationCompat.Builder(mContext)
//                    .setContentTitle(title)
//                    .setContentText(text)
//                    .setWhen(when)
//                    .setSmallIcon(R.mipmap.ic_launcher);
//
//            mNotifyBuilder.setContentIntent(resultPendingIntent);
//
//            int defaults = 0;
//            defaults = defaults | NotificationFragment.DEFAULT_LIGHTS;
//            defaults = defaults | NotificationFragment.DEFAULT_VIBRATE;
//            defaults = defaults | NotificationFragment.DEFAULT_SOUND;
//
//            mNotifyBuilder.setDefaults(defaults);
//            mNotifyBuilder.setAutoCancel(true);
//
//            mNotificationManager.notify(id, mNotifyBuilder.build());
//        }
//    }

    public static SharedPreferences.Editor editSharedPref(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        return editor;
    }
    public static SharedPreferences getSharedPref(Context mContext) {
        return  mContext.getSharedPreferences("settings", 0);
    }


}
