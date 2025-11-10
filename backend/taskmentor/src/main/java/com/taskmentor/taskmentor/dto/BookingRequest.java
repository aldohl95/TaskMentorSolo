package com.taskmentor.taskmentor.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class BookingRequest {

    @NotNull(message = "Mentor ID is required")
    private Long mentorId;

    @NotNull(message = "Task ID is required")
    private Long taskId;

    @NotNull(message = "Proposed date is required")
    private LocalDateTime proposedDate;

    private String studentMsg;

    // Constructors
    public BookingRequest() {}

    public BookingRequest(Long mentorId, Long taskId, LocalDateTime proposedDate, String studentMsg) {
        this.mentorId = mentorId;
        this.taskId = taskId;
        this.proposedDate = proposedDate;
        this.studentMsg = studentMsg;
    }

    // Getters and Setters
    public Long getMentorId() {
        return mentorId;
    }

    public void setMentorId(Long mentorId) {
        this.mentorId = mentorId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public LocalDateTime getProposedDate() {
        return proposedDate;
    }

    public void setProposedDate(LocalDateTime proposedDate) {
        this.proposedDate = proposedDate;
    }

    public String getStudentMsg() {
        return studentMsg;
    }

    public void setStudentMsg(String studentMsg) {
        this.studentMsg = studentMsg;
    }
}