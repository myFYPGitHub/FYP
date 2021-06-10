package com.fyp.Beauticianatyourdoorstep.model;

public class User extends Person {
    private String email, password, contact, address, city, category, profilePicStorageId, profilePicUri;
    private Integer totalRating, numOfRating;

    public User() {
        super("", "", "", "");
    }

    public User(String firstName, String lastName, String age, String gender, String email, String password, String contact, String address, String city, String category) {
        super(firstName, lastName, age, gender);
        this.email = email;
        this.password = password;
        this.contact = contact;
        this.address = address;
        this.city = city;
        this.category = category;
        this.totalRating = 0;
        this.numOfRating = 0;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCategory() {
        return category;
    }

    public String getProfilePicStorageId() {
        return profilePicStorageId;
    }

    public void setProfilePicStorageId(String profilePicStorageId) {
        this.profilePicStorageId = profilePicStorageId;
    }

    public Integer getTotalRating() {
        return totalRating;
    }

    public void setTotalRating(Integer totalRating) {
        this.totalRating = totalRating;
    }

    public Integer getNumOfRating() {
        return numOfRating;
    }

    public void setNumOfRating(Integer numOfRating) {
        this.numOfRating = numOfRating;
    }

    public String getProfilePicUri() {
        return profilePicUri;
    }

    public void setProfilePicUri(String profilePicUri) {
        this.profilePicUri = profilePicUri;
    }
}
