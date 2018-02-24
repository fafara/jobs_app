package com.me.njerucyrus.models;

import java.util.Date;

/**
 * Created by njerucyrus on 2/6/18.
 */

public class JobPost {
    private String category;
    private String description;
    private String title;
    private String location;
    private double lat;
    private double lng;
    private Date postedOn;
    private String postedBy;
    private String postedByUid;
    private String deadline;


    public JobPost() {}

    public JobPost(String category, String description, String title, String location, double lat, double lng, Date postedOn, String postedBy, String postedByUid, String deadline) {
        this.category = category;
        this.description = description;
        this.title = title;
        this.location = location;
        this.lat = lat;
        this.lng = lng;
        this.postedOn = postedOn;
        this.postedBy = postedBy;
        this.postedByUid = postedByUid;
        this.deadline = deadline;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public Date getPostedOn() {
        return postedOn;
    }

    public void setPostedOn(Date postedOn) {
        this.postedOn = postedOn;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public String getPostedByUid() {
        return postedByUid;
    }

    public void setPostedByUid(String postedByUid) {
        this.postedByUid = postedByUid;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
}
