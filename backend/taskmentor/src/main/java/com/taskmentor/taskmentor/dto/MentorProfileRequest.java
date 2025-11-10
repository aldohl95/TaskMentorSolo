package com.taskmentor.taskmentor.dto;

import jakarta.validation.constraints.NotBlank;

public class MentorProfileRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private String bio;

    @NotBlank(message = "Role title is required")
    private String roleTitle;

    @NotBlank(message = "Company is required")
    private String company;

    private String yrsExp;
    private String industries;
    private String expertise;
    private String photoUrl;

    // Constructors
    public MentorProfileRequest() {}

    public MentorProfileRequest(String firstName, String lastName, String bio, String roleTitle,
                                String company, String yrsExp, String industries, String expertise, String photoUrl) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.bio = bio;
        this.roleTitle = roleTitle;
        this.company = company;
        this.yrsExp = yrsExp;
        this.industries = industries;
        this.expertise = expertise;
        this.photoUrl = photoUrl;
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

    public String getRoleTitle() {
        return roleTitle;
    }

    public void setRoleTitle(String roleTitle) {
        this.roleTitle = roleTitle;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getYrsExp() {
        return yrsExp;
    }

    public void setYrsExp(String yrsExp) {
        this.yrsExp = yrsExp;
    }

    public String getIndustries() {
        return industries;
    }

    public void setIndustries(String industries) {
        this.industries = industries;
    }

    public String getExpertise() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}