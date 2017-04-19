package com.example.android.shelterforpets.DatabaseObjects;


public class Admin {

    private String firstName;
    private String lastName;
    private boolean superUser;

    public Admin(String firstName, String lastName, boolean superUser) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.superUser = superUser;
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

    public boolean getSuperUser() {
        return superUser;
    }

    public void setSuperUser(boolean superUser) {
        this.superUser = superUser;
    }
}
