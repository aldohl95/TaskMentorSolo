package com.taskmentor.taskmentor.model;


import jakarta.persistence.*;

import com.taskmentor.taskmentor.model.Task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mentors")
public class MentorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mentor_seq")
    @SequenceGenerator(name = "mentor_seq", sequenceName = "mentors_mentor_id_seq", allocationSize = 1)
    @Column(name = "mentor_id", nullable = false, updatable = false)
    private Long mentorId;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", unique = true)
    private User user;

    @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();

    @Column(name = "first_name")
    private String firstName;

    @Column (name = "last_name")
    private String lastName;

    private String bio;

    @Column(name = "role_title")
    private String roleTitle;

    private String company;

    @Column(name = "yrs_exp")
    private String yrsExp;

    private String industries;
    private String expertise;

    @Column(name = "photo_url")
    private String photoUrl;

    public MentorProfile() {

    }

    public MentorProfile(Long mentorId, User user, List<Task> tasks, String firstName, String lastName, String bio,
                         String roleTitle, String company, String yrsExp, String industries, String expertise, String photoUrl) {
        this.mentorId = mentorId;
        this.user = user;
        this.tasks = tasks;
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

    public Long getMentorId() {
        return mentorId;
    }

    public void setMentorId(Long mentorId) {
        this.mentorId = mentorId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
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

    @Override
    public String toString() {
        return "MentorProfile{" +
                "MentorId" + mentorId +
                "First Name" + firstName + '\'' +
                "Last Name" + lastName + '\'' +
                "Bio" + bio + '\'' +
                "RoleTitle" + roleTitle + '\'' +
                "Company" + company + '\'' +
                "YearsExp" + yrsExp + '\'' +
                "Industries" + industries + '\'' +
                "Expertise" + expertise + '\''+
                '}';

    }
}
