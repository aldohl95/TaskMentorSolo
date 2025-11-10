package com.taskmentor.taskmentor.dto;

import com.taskmentor.taskmentor.enums.Status;

import java.time.LocalDateTime;

public class BookingResponse {
    private Long bookingId;
    private StudentProfileResponse student;
    private MentorProfileResponse mentor;
    private TaskResponse task;
    private LocalDateTime proposedDate;
    private Status status;
    private String studentMsg;
    private String mentorResp;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public BookingResponse() {}

    public BookingResponse(Long bookingId, StudentProfileResponse student, MentorProfileResponse mentor,
                           TaskResponse task, LocalDateTime proposedDate, Status status, String studentMsg,
                           String mentorResp, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.bookingId = bookingId;
        this.student = student;
        this.mentor = mentor;
        this.task = task;
        this.proposedDate = proposedDate;
        this.status = status;
        this.studentMsg = studentMsg;
        this.mentorResp = mentorResp;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public StudentProfileResponse getStudent() {
        return student;
    }

    public void setStudent(StudentProfileResponse student) {
        this.student = student;
    }

    public MentorProfileResponse getMentor() {
        return mentor;
    }

    public void setMentor(MentorProfileResponse mentor) {
        this.mentor = mentor;
    }

    public TaskResponse getTask() {
        return task;
    }

    public void setTask(TaskResponse task) {
        this.task = task;
    }

    public LocalDateTime getProposedDate() {
        return proposedDate;
    }

    public void setProposedDate(LocalDateTime proposedDate) {
        this.proposedDate = proposedDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getStudentMsg() {
        return studentMsg;
    }

    public void setStudentMsg(String studentMsg) {
        this.studentMsg = studentMsg;
    }

    public String getMentorResp() {
        return mentorResp;
    }

    public void setMentorResp(String mentorResp) {
        this.mentorResp = mentorResp;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}