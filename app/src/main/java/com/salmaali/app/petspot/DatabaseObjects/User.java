package com.salmaali.app.petspot.DatabaseObjects;


public class User {
    private String firstName;
    private String lastName;
    private String photoUrl;
    private boolean volunteer;

    public User(String firstName, String lastName, boolean volunteer) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.volunteer = volunteer;
    }

    public User(String firstName, String lastName, String photoUrl, boolean volunteer) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.photoUrl = photoUrl;
        this.volunteer = volunteer;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public boolean isVolunteer() {
        return volunteer;
    }

    public void setVolunteer(boolean volunteer) {
        this.volunteer = volunteer;
    }
}
