package com.fyp.Beauticianatyourdoorstep.model;

import java.io.Serializable;

public interface PersonInterface extends Serializable {
    String getFirstName();

    String getLastName();

    String getAge();

    String getGender();

    String getEmail();

    String getPassword();

    String getContact();

    String getAddress();

    String getCity();

    String getCategory();

    String getProfilePicStorageId();

    Integer getTotalRating();

    Integer getNumOfRating();

    String getProfilePicUri();
}
