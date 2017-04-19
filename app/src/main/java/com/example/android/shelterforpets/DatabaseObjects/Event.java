package com.example.android.shelterforpets.DatabaseObjects;


public class Event {

    private String eventName;
    private String eventDescription;
    private String eventDate;
    private String eventFromTime;
    private String eventToTime;
    private String eventPhoto;

    public Event(String eventName, String eventDescription, String eventDate, String eventFromTime, String eventToTime, String eventPhoto) {
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventDate = eventDate;
        this.eventFromTime = eventFromTime;
        this.eventToTime = eventToTime;
        this.eventPhoto = eventPhoto;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventFromTime() {
        return eventFromTime;
    }

    public void setEventFromTime(String eventFromTime) {
        this.eventFromTime = eventFromTime;
    }

    public String getEventToTime() {
        return eventToTime;
    }

    public void setEventToTime(String eventToTime) {
        this.eventToTime = eventToTime;
    }

    public String getEventPhoto() {
        return eventPhoto;
    }

    public void setEventPhoto(String eventPhoto) {
        this.eventPhoto = eventPhoto;
    }
}
