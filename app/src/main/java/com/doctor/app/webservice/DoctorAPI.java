package com.doctor.app.webservice;

import com.doctor.app.model.AppointmentPojo;
import com.doctor.app.model.PrescriptionPojo;
import com.doctor.app.model.SuccessPojo;
import com.doctor.app.model.UserPojo;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by SWIFT-3 on 17/07/17.
 */

public interface DoctorAPI {

    @POST("doctor_edit_profile.php")
    Call<SuccessPojo> editProfile(@Body RequestBody body);

    @FormUrlEncoded
    @POST("upcoming_appointment.php")
    Call<List<AppointmentPojo>> getUpcoming(
            @Field("doctor_id") String doctor_id
    );

    @FormUrlEncoded
    @POST("todays_appointment.php")
    Call<List<AppointmentPojo>> getTodays(
            @Field("doctor_id") String doctor_id
    );

    @FormUrlEncoded
    @POST("past_appointment.php")
    Call<List<AppointmentPojo>> getHistory(
            @Field("doctor_id") String doctor_id
    );

    @FormUrlEncoded
    @POST("appointment_status_update.php")
    Call<SuccessPojo> updateStatus(
            @Field("doctor_id") String doctor_id,
            @Field("appointment_id") String appointment_id,
            @Field("status") String status,
            @Field("prescription") String prescription
    );

    @FormUrlEncoded
    @POST("delete_clinic_image.php")
    Call<SuccessPojo> deleteImage(
            @Field("clinic_img_id") String clinic_img_id);

    @FormUrlEncoded
    @POST("update_doc_availibility.php")
    Call<SuccessPojo> updateDrStaus(
            @Field("availbility_status") String availbility_status,
            @Field("doctor_id") String doctor_id);


}
