package com.taskmentor.taskmentor.dto;

import java.util.List;

public class MentorProfileResponse {
    private Long mentorId;
    private Long userId;
    private String firstName;
    private String lastName;
    private String bio;
    private String roleTitle;
    private String company;
    private String yrsExp;
    private String industries;
    private String expertise;
    private String photoUrl;
    private List<TaskResponse> tasks;

    // Constructors
    public MentorProfileResponse() {}

    public MentorProfileResponse(Long mentorId, Long userId, String firstName, String lastName,
                                 String bio, String roleTitle, String company, String yrsExp,
                                 String industries, String expertise, String photoUrl, List<TaskResponse> tasks) {
        this.mentorId = mentorId;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.bio = bio;
        this.roleTitle = roleTitle;
        this.company = company;
        this.yrsExp = yrsExp;
        this.industries = industries;
        this.expertise = expertise;
        this.photoUrl = photoUrl;
        this.tasks = tasks;
    }

    // Getters and Setters
    public Long getMentorId() {
        return mentorId;
    }

    public void setMentorId(Long mentorId) {
        this.mentorId = mentorId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public List<TaskResponse> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskResponse> tasks) {
        this.tasks = tasks;
    }
}
