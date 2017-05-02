package com.salmaali.app.petspot.DatabaseObjects;


public class Shelter {

    private String shelterName;
    private String shelterAddress;

    public Shelter(String shelterName, String shelterAddress) {
        this.shelterName = shelterName;
        this.shelterAddress = shelterAddress;
    }

    public String getShelterName() {
        return shelterName;
    }

    public void setShelterName(String shelterName) {
        this.shelterName = shelterName;
    }

    public String getShelterAddress() {
        return shelterAddress;
    }

    public void setShelterAddress(String shelterAddress) {
        this.shelterAddress = shelterAddress;
    }
}
