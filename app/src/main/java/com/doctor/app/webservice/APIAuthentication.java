package com.doctor.app.webservice;

import com.doctor.app.model.AppointmentPojo;
import com.doctor.app.model.CityPojo;
import com.doctor.app.model.DepartmentPojo;
import com.doctor.app.model.DoctorDetailPojo;
import com.doctor.app.model.DoctorPojo;
import com.doctor.app.model.HospitalPojo;
import com.doctor.app.model.NotificationPojo;
import com.doctor.app.model.PrescriptionPojo;
import com.doctor.app.model.SuccessPojo;
import com.doctor.app.model.TimingSlots;
import com.doctor.app.model.UserPojo;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by SWIFT-3 on 18/05/17.
 */

public interface APIAuthentication {

    @FormUrlEncoded
    @POST("login.php")
    Call<UserPojo> login(
            @Field("email") String email,
            @Field("password") String password
    );

    @Multipart
    @POST("patient_register.php")
    Call<UserPojo> register(@Part("name") RequestBody name,
                            @Part("password") RequestBody password,
                            @Part("email") RequestBody email,
                            @Part("mobile_no") RequestBody mobile_no,
                            @Part("gender") RequestBody gender,
                            @Part("city") RequestBody city,
                            @Part("login_type") RequestBody login_type,
                            @Part MultipartBody.Part photo
    );

    @Multipart
    @POST("edit_profile.php")
    Call<UserPojo> editProfile(@Part("patient_id") RequestBody patient_id,
                               @Part("name") RequestBody name,
                            @Part("mobile_no") RequestBody mobile_no,
                            @Part("gender") RequestBody gender,
                            @Part MultipartBody.Part photo
    );

    @FormUrlEncoded
    @POST("change_pwd.php")
    Call<SuccessPojo> changePassword(
            @Field("patient_id") String patient_id,
            @Field("current_pwd") String current_pwd,
            @Field("new_pwd") String new_pwd
    );

    @FormUrlEncoded
    @POST("forgot_pwd.php")
    Call<SuccessPojo> forgotPassword(
            @Field("email") String email
    );

    @POST("get_city_list.php")
    Call<List<CityPojo>> getCities();

    @FormUrlEncoded
    @POST("get_dept_list.php")
    Call<List<DepartmentPojo>> getDepartmentByCity(
            @Field("city") String city
    );

    @FormUrlEncoded
    @POST("get_doctors_list.php")
    Call<List<DoctorPojo>> getDoctors(
            @Field("city") String city,
            @Field("department_id") String department_id
    );

    @FormUrlEncoded
    @POST("get_doctor_details.php")
    Call<DoctorDetailPojo> getDoctorDetail(
            @Field("doctor_id") String doctor_id
    );

    @FormUrlEncoded
    @POST("get_timing_slots.php")
    Call<TimingSlots> getTiming(
            @Field("doctor_id") String doctor_id,
            @Field("appointment_date") String appointment_date,
            @Field("day") String day
    );

    @FormUrlEncoded
    @POST("add_appointment.php")
    Call<SuccessPojo> bookAppointment(
            @Field("doctor_id") String doctor_id,
            @Field("adate") String adate,
            @Field("timing") String timing,
            @Field("patient_id") String patient_id,
            @Field("note") String note
    );

    @FormUrlEncoded
    @POST("appointment_details.php")
    Call<List<AppointmentPojo>> getAppointments(
            @Field("patient_id") String patient_id
    );

    @FormUrlEncoded
    @POST("near_by_hospitals.php")
    Call<List<DoctorPojo>> getHospitals(
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("distance") String distance
    );

    @FormUrlEncoded
    @POST("update_gcm.php")
    Call<SuccessPojo> updateFcm(@Field("user_id") String user_id,
                                @Field("role") String role,
                                @Field("gcm_token") String gcm_token
    );

    @FormUrlEncoded
    @POST("add_review.php")
    Call<SuccessPojo> addReview(
            @Field("doctor_id") String doctor_id,
            @Field("patient_id") String patient_id,
            @Field("ratings") String ratings,
            @Field("review_text") String review_text);


    @FormUrlEncoded
    @POST("get_notification_list.php")
    Call<List<NotificationPojo>> getData(
            @Field("user_id") String user_id,
            @Field("role") String role);

    @FormUrlEncoded
    @POST("prescription_doc_list.php")
    Call<List<DoctorPojo>> doctorList(
            @Field("patient_id") String patient_id);

    @FormUrlEncoded
    @POST("prescription_list.php")
    Call<List<PrescriptionPojo>> getPrescriptions(
            @Field("patient_id") String patient_id,
            @Field("doctor_id") String doctor_id);
}
