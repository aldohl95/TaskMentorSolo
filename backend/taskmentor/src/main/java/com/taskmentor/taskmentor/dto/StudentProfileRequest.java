package com.taskmentor.taskmentor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

public class StudentProfileRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must be at most 100 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must be at most 100 characters")
    private String lastName;

    @Size(max = 2000, message = "Bio must be at most 2000 characters")
    private String bio;
    @Size(max = 100, message = "Major must be at most 100 characters")
    private String major;
    @Min(value = 1990, message = "Graduation year is out of range")
    @Max(value = 2100, message = "Graduation year is out of range")
    private Integer gradYear;
    @Size(max = 1000, message = "Interests must be at most 1000 characters")
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