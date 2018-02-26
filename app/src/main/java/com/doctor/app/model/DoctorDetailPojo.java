package com.doctor.app.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoctorDetailPojo {

    private String id;
    private String doctor_name,education_details;
    private String period;
    private String availbility_status;
    private String email;
    private String city;
    private String address;
    private String clinic_name;
    private String doctor_image;
    private String clinic_phone;
    private String personal_phone;
    private String about,distance;
    private String gcm_token;
    private String latitude;
    private String longitude;
    private List<DepartmentPojo> department = null;
    private List<Duty_timing> duty_timing = null;
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

    public List<Duty_timing> getDuty_timing() {
        return duty_timing;
    }

    public void setDuty_timing(List<Duty_timing> duty_timing) {
        this.duty_timing = duty_timing;
    }

    public List<Clinic_image> getClinic_images() {
        return clinic_images;
    }

    public void setClinic_images(List<Clinic_image> clinic_images) {
        this.clinic_images = clinic_images;
    }

    public String getEducation_details() {
        return education_details;
    }

    public void setEducation_details(String education_details) {
        this.education_details = education_details;
    }

    public List<DepartmentPojo> getDepartment() {
        return department;
    }

    public void setDepartment(List<DepartmentPojo> department) {
        this.department = department;
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

    public String getDistance() {
        return distance;
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
