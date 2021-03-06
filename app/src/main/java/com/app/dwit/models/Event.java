package com.app.dwit.models;

public class Event extends Super {
    String eventId;
    String imageUrl;
    String address;
    String title;
    String description;
    String date;
    String startTime;
    String endTime;
    String lat;
    String lng;
    String endTimeInMillis;

    public Event() {
    }

    public Event(String eventId, String imageUrl, String address, String title, String description,
                 String date, String startTime, String endTime, String lat,
                 String lng, String endTimeInMillis) {
        this.eventId = eventId;
        this.imageUrl = imageUrl;
        this.address = address;
        this.title = title;
        this.endTimeInMillis = endTimeInMillis;
        this.description = description;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.lat = lat;
        this.lng = lng;
    }

    public String getEndTimeInMillis() {
        return endTimeInMillis;
    }

    public void setEndTimeInMillis(String endTimeInMillis) {
        this.endTimeInMillis = endTimeInMillis;
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

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
