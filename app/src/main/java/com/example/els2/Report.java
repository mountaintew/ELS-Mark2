package com.example.els2;

public class Report {
    String lat, lng, locality, state, details, message, mobile, fullname;
    boolean toggled;

    public Report(String lat, String lng, String locality, String state, String details, String message, String mobile, String fullname, boolean toggled) {
        this.lat = lat;
        this.lng = lng;
        this.locality = locality;
        this.state = state;
        this.details = details;
        this.message = message;
        this.mobile = mobile;
        this.fullname = fullname;
        this.toggled = toggled;
    }

    public Report() {
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public boolean isToggled() {
        return toggled;
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
    }
}
