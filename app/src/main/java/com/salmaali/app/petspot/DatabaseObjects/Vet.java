package com.salmaali.app.petspot.DatabaseObjects;


public class Vet {
    private String vetName;
    private String drName;
    private String vetAddress;
    private String telephoneNumber;
    private String vetOpenFrom;
    private String vetOpenTo;

    public Vet(String vetName, String drName, String vetAddress, String telephoneNumber, String vetOpenFrom, String vetOpenTo) {
        this.vetName = vetName;
        this.drName = drName;
        this.vetAddress = vetAddress;
        this.telephoneNumber = telephoneNumber;
        this.vetOpenFrom = vetOpenFrom;
        this.vetOpenTo = vetOpenTo;
    }

    public String getVetName() {
        return vetName;
    }

    public void setVetName(String vetName) {
        this.vetName = vetName;
    }

    public String getDrName() {
        return drName;
    }

    public void setDrName(String drName) {
        this.drName = drName;
    }

    public String getVetAddress() {
        return vetAddress;
    }

    public void setVetAddress(String vetAddress) {
        this.vetAddress = vetAddress;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getVetOpenFrom() {
        return vetOpenFrom;
    }

    public void setVetOpenFrom(String vetOpenFrom) {
        this.vetOpenFrom = vetOpenFrom;
    }

    public String getVetOpenTo() {
        return vetOpenTo;
    }

    public void setVetOpenTo(String vetOpenTo) {
        this.vetOpenTo = vetOpenTo;
    }
}
