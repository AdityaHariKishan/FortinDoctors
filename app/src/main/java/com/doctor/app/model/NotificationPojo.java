package com.doctor.app.model;

import java.util.HashMap;
import java.util.Map;

public class NotificationPojo {

        private String id;
        private String department_id;
        private String doctor_id;
        private String appointment_date;
        private String appointment_time;
        private String patient_id;
        private String note;
        private String appointment_status;
        private String created_time;
        private String doctor_name;
        private String doctor_image;
        private String doc_gcm_token;
        private String patient_name;
        private String patient_photo;
        private String patient_gcm_token;
        private String patient_email;
        private String message;
        private String time;
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDepartment_id() {
            return department_id;
        }

        public void setDepartment_id(String department_id) {
            this.department_id = department_id;
        }

        public String getDoctor_id() {
            return doctor_id;
        }

        public void setDoctor_id(String doctor_id) {
            this.doctor_id = doctor_id;
        }

        public String getAppointment_date() {
            return appointment_date;
        }

        public void setAppointment_date(String appointment_date) {
            this.appointment_date = appointment_date;
        }

        public String getAppointment_time() {
            return appointment_time;
        }

        public void setAppointment_time(String appointment_time) {
            this.appointment_time = appointment_time;
        }

        public String getPatient_id() {
            return patient_id;
        }

        public void setPatient_id(String patient_id) {
            this.patient_id = patient_id;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public String getAppointment_status() {
            return appointment_status;
        }

        public void setAppointment_status(String appointment_status) {
            this.appointment_status = appointment_status;
        }

        public String getCreated_time() {
            return created_time;
        }

        public void setCreated_time(String created_time) {
            this.created_time = created_time;
        }

        public String getDoctor_name() {
            return doctor_name;
        }

        public void setDoctor_name(String doctor_name) {
            this.doctor_name = doctor_name;
        }

        public String getDoctor_image() {
            return doctor_image;
        }

        public void setDoctor_image(String doctor_image) {
            this.doctor_image = doctor_image;
        }

        public String getDoc_gcm_token() {
            return doc_gcm_token;
        }

        public void setDoc_gcm_token(String doc_gcm_token) {
            this.doc_gcm_token = doc_gcm_token;
        }

        public String getPatient_name() {
            return patient_name;
        }

        public void setPatient_name(String patient_name) {
            this.patient_name = patient_name;
        }

        public String getPatient_photo() {
            return patient_photo;
        }

        public void setPatient_photo(String patient_photo) {
            this.patient_photo = patient_photo;
        }

        public String getPatient_gcm_token() {
            return patient_gcm_token;
        }

        public void setPatient_gcm_token(String patient_gcm_token) {
            this.patient_gcm_token = patient_gcm_token;
        }

        public String getPatient_email() {
            return patient_email;
        }

        public void setPatient_email(String patient_email) {
            this.patient_email = patient_email;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public Map<String, Object> getAdditionalProperties() {
            return this.additionalProperties;
        }

        public void setAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
        }

}
