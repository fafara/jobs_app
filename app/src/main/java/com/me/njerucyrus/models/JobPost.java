package com.me.njerucyrus.models;

/**
 * Created by njerucyrus on 2/6/18.
 */

public class JobPost {
    private String category;
    private String description;
    private String title;
    private String location;
    private Float lat;
    private Float lng;
    private Float salary;
    private String postedOn;
    private String deadline;
    private String postedBy;

    public JobPost(){}

    public JobPost(String category, String description,
                   String title, String location,
                   Float lat, Float lng,
                   Float salary, String postedOn,
                   String deadline,
                   String postedBy) {
        this.category = category;
        this.description = description;
        this.title = title;
        this.location = location;
        this.lat = lat;
        this.lng = lng;
        this.salary = salary;
        this.postedOn = postedOn;
        this.deadline = deadline;
        this.postedBy = postedBy;
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

    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public Float getLng() {
        return lng;
    }

    public void setLng(Float lng) {
        this.lng = lng;
    }

    public Float getSalary() {
        return salary;
    }

    public void setSalary(Float salary) {
        this.salary = salary;
    }

    public String getPostedOn() {
        return postedOn;
    }

    public void setPostedOn(String postedOn) {
        this.postedOn = postedOn;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }
}
