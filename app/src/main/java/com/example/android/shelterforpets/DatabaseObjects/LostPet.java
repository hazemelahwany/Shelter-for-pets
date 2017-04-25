package com.example.android.shelterforpets.DatabaseObjects;


public class LostPet {

    private String downloadURL;
    private String location;

    public LostPet(String downloadURL, String location) {
        this.downloadURL = downloadURL;
        this.location = location;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
