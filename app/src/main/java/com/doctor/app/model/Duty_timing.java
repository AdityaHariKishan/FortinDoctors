package com.doctor.app.model;

import java.util.HashMap;
import java.util.Map;

public class Duty_timing {

    private String id;
    private String doctor_id;
    private String days;
    private String duty_start_time;
    private String duty_end_time;
    private String break_time;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(String doctor_id) {
        this.doctor_id = doctor_id;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getDuty_start_time() {
        return duty_start_time;
    }

    public void setDuty_start_time(String duty_start_time) {
        this.duty_start_time = duty_start_time;
    }

    public String getDuty_end_time() {
        return duty_end_time;
    }

    public void setDuty_end_time(String duty_end_time) {
        this.duty_end_time = duty_end_time;
    }

    public String getBreak_time() {
        return break_time;
    }

    public void setBreak_time(String break_time) {
        this.break_time = break_time;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}