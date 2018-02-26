package com.doctor.app.helper;

import com.doctor.app.model.AppointmentPojo;
import com.doctor.app.model.DoctorDetailPojo;
import com.doctor.app.model.DoctorPojo;
import com.doctor.app.model.HospitalPojo;
import com.doctor.app.model.NotificationPojo;

import java.util.ArrayList;

public class AppConst {

    public static String TAG="DoctorApp";

//    public static final String serverurl ="http://funtoossh.in/FortinDoctors/";//  Replace your URL here...
    public static final String serverurl ="http://172.16.22.34/DoctorReactNative/";//  Replace your URL here...
    public static final String MAIN =serverurl+"webservice/";
    public static final String profile_img_url=serverurl+"upload/patient/";
    public static final String dept_img_url=serverurl+"upload/dept/";
    public static final String doctor_img_url=serverurl+"upload/doctor/";
    public static final String clinic_img_url=serverurl+"upload/clinic/";

    public static String appurl = "https://play.google.com/store/apps/details?id=com.fortin.doctor";  // replace your package name here for share

    public static String share = "You can download app from : "+appurl;
    public static int distance=10;
    public static String fcm_id="";

    public static DoctorDetailPojo selDoctor;
    public static DoctorPojo seldr;
    public static AppointmentPojo selAppointment;
    public static ArrayList<DoctorPojo> hospitalPojoArrayList;
    public static HospitalPojo selHospital;
    public static NotificationPojo selNotification;
    public static String status_accept="accepted" ,status_complete="completed",status_cancel="cancelled";
}
