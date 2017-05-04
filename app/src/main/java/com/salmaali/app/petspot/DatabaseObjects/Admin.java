package com.salmaali.app.petspot.DatabaseObjects;

/**
 * Object Admin class used to store admin in Firebase database.
 * @author Hazem El Ahwany
 * @since 2017-04-05
 */
public class Admin {

    /**
     * A String for Admin's first name.
     */
    private String firstName;
    /**
     * A String for Admin's last name.
     */
    private String lastName;

    /**
     * Constructor.
     * @param firstName this should be admin's first name String
     * @param lastName  this should be admin's last name String
     */
    public Admin(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Getter to get first name.
     * @return  this should return a String contains first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Setter to set Admin's first name.
     * @param firstName String that contains first name of admin
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Getter to get last name.
     * @return  this should return a String contains last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Setter to set Admin's last name.
     * @param lastName  String that contains last name of admin
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

}
