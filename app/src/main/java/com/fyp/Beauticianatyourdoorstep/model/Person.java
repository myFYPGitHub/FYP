package com.fyp.Beauticianatyourdoorstep.model;

public abstract class Person implements PersonInterface {
    private String firstName, lastName, age, gender;

    public Person(String firstName, String lastName, String age, String gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.gender = gender;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }
}
