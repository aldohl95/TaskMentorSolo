package com.taskmentor.taskmentor.dto;

import jakarta.validation.constraints.NotBlank;

public class StudentProfileRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private String bio;
    private String major;
    private Integer gradYear;
    private String interests;

    // Constructors
    public StudentProfileRequest() {}

    public StudentProfileRequest(String firstName, String lastName, String bio,
                                 String major, Integer gradYear, String interests) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.bio = bio;
        this.major = major;
        this.gradYear = gradYear;
        this.interests = interests;
    }

    // Getters and Setters
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

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public Integer getGradYear() {
        return gradYear;
    }

    public void setGradYear(Integer gradYear) {
        this.gradYear = gradYear;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }
}