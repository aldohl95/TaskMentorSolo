package com.taskmentor.taskmentor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MentorProfileRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must be at most 100 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must be at most 100 characters")
    private String lastName;

    @Size(max = 2000, message = "Bio must be at most 2000 characters")
    private String bio;

    @NotBlank(message = "Role title is required")
    @Size(max = 100, message = "Role title must be at most 100 characters")
    private String roleTitle;

    @NotBlank(message = "Company is required")
    @Size(max = 100, message = "Company must be at most 100 characters")
    private String company;

    @Size(max = 50, message = "Years of experience must be at most 50 characters")
    private String yrsExp;
    @Size(max = 1000, message = "Industries must be at most 1000 characters")
    private String industries;
    @Size(max = 1000, message = "Expertise must be at most 1000 characters")
    private String expertise;
    @Size(max = 500, message = "Photo URL must be at most 500 characters")
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