package com.taskmentor.taskmentor.model;


import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "students")
public class StudentProfile{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "student_seq")
    @SequenceGenerator(name = "student_seq", sequenceName = "students_student_id_seq",  allocationSize = 1)
    @Column(name = "student_id", nullable = false, updatable = false)
    private Long studentId;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", unique = true)
    private User user;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String bio;

    private String major;

    @Column(name = "graduation_Year")
    private Integer gradYear;

    @Column(name = "career_interests")
    private String interests;

    public StudentProfile() {

    }

    public StudentProfile(Long studentId, User user, String firstName, String lastName, String bio, String major,
                          Integer gradYear, String interests) {
        this.studentId = studentId;
        this.user = user;
        this.firstName = firstName;
        this.lastName = lastName;
        this.bio = bio;
        this.major = major;
        this.gradYear = gradYear;
        this.interests = interests;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    @Override
    public String toString() {
        return "StudentProfile{" +
                "StudentId" + studentId + '\'' +
                "First Name" + firstName + '\'' +
                "Last Name" + lastName + '\'' +
                "Bio" + bio + '\'' +
                "Major" + major + '\'' +
                "GradYear" + gradYear + '\'' +
                "Interests" + interests + '}';
    }
}
