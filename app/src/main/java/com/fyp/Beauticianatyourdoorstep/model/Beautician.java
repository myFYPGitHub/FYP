package com.fyp.Beauticianatyourdoorstep.model;

public class Beautician extends User {
    private String description, specialization;
    private Boolean availability;

    public Beautician() {   //Required Constructor for Firebase
        super("", "", "", "", "", "", "", "", "", "");
    }

    public Beautician(String firstName, String lastName, String age, String gender, String email, String password, String contact, String address, String city, String category, String specialization, Boolean availability) {
        super(firstName, lastName, age, gender, email, password, contact, address, city, category);
        this.specialization = specialization;
        this.availability = availability;
    }

    public String getDescription() {
        return description;
    }

    public String getSpecialization() {
        return specialization;
    }

    public Boolean getAvailability() {
        return availability;
    }

}
