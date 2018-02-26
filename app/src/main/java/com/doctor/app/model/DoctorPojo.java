package com.doctor.app.model;

import java.util.List;

/**
 * Created by SWIFT-3 on 10/07/17.
 */

public class DoctorPojo {

    String id,doctor_name,clinic_name,doctor_image,education_details,distance;
    private List<DepartmentPojo> department = null;
    private Double overall_rating;
    private String latitude,longitude;//only for nearby

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

    public String getEducation_details() {
        return education_details;
    }

    public void setEducation_details(String education_details) {
        this.education_details = education_details;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public List<DepartmentPojo> getDepartment() {
        return department;
    }

    public void setDepartment(List<DepartmentPojo> department) {
        this.department = department;
    }

    public Double getOverall_rating() {
        return overall_rating;
    }

    public void setOverall_rating(Double overall_rating) {
        this.overall_rating = overall_rating;
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
}
