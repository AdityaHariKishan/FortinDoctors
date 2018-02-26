package com.doctor.app.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HospitalPojo {

    private String id;
    private String doctor_name;
    private String department_id;
    private String period;
    private String availbility_status;
    private String email;
    private String password;
    private String city;
    private String address;
    private String clinic_name;
    private String doctor_image;
    private String clinic_phone;
    private String personal_phone;
    private String about;
    private String gcm_token;
    private String latitude;
    private String longitude;
    private String distance;
    private List<Clinic_image> clinic_images = null;
    private List<Rating> ratings = null;
    private Double overall_rating;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDoctor_name() {
        return doctor_name;
    }

    public void setDoctor_name(String doctor_name) {
        this.doctor_name = doctor_name;
    }

    public String getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(String department_id) {
        this.department_id = department_id;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getAvailbility_status() {
        return availbility_status;
    }

    public void setAvailbility_status(String availbility_status) {
        this.availbility_status = availbility_status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getClinic_name() {
        return clinic_name;
    }

    public void setClinic_name(String clinic_name) {
        this.clinic_name = clinic_name;
    }

    public String getDoctor_image() {
        return doctor_image;
    }

    public void setDoctor_image(String doctor_image) {
        this.doctor_image = doctor_image;
    }

    public String getClinic_phone() {
        return clinic_phone;
    }

    public void setClinic_phone(String clinic_phone) {
        this.clinic_phone = clinic_phone;
    }

    public String getPersonal_phone() {
        return personal_phone;
    }

    public void setPersonal_phone(String personal_phone) {
        this.personal_phone = personal_phone;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getGcm_token() {
        return gcm_token;
    }

    public void setGcm_token(String gcm_token) {
        this.gcm_token = gcm_token;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public List<Clinic_image> getClinic_images() {
        return clinic_images;
    }

    public void setClinic_images(List<Clinic_image> clinic_images) {
        this.clinic_images = clinic_images;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public Double getOverall_rating() {
        return overall_rating;
    }

    public void setOverall_rating(Double overall_rating) {
        this.overall_rating = overall_rating;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
