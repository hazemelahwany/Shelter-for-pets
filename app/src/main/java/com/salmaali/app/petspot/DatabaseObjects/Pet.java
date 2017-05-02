package com.salmaali.app.petspot.DatabaseObjects;


public class Pet {
    String photoUrl;
    String petName;
    String petType;
    String petBreed;
    String petAge;
    String petGender;
    String userID;

    public Pet(String petName, String petType, String petBreed, String petAge, String petGender, String userID) {
        this.petName = petName;
        this.petType = petType;
        this.petBreed = petBreed;
        this.petAge = petAge;
        this.petGender = petGender;
        this.userID = userID;
    }

    public Pet(String photoUrl, String petName, String petType, String petBreed, String petAge, String petGender, String userID) {
        this.photoUrl = photoUrl;
        this.petName = petName;
        this.petType = petType;
        this.petBreed = petBreed;
        this.petAge = petAge;
        this.petGender = petGender;
        this.userID = userID;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getPetType() {
        return petType;
    }

    public void setPetType(String petType) {
        this.petType = petType;
    }

    public String getPetBreed() {
        return petBreed;
    }

    public void setPetBreed(String petBreed) {
        this.petBreed = petBreed;
    }

    public String getPetAge() {
        return petAge;
    }

    public void setPetAge(String petAge) {
        this.petAge = petAge;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
