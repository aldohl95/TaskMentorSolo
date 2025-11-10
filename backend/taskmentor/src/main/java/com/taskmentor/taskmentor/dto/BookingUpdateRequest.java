package com.taskmentor.taskmentor.dto;

import com.taskmentor.taskmentor.enums.Status;
import jakarta.validation.constraints.NotNull;

public class BookingUpdateRequest {

    @NotNull(message = "Status is required")
    private Status status;

    private String mentorResp;

    // Constructors
    public BookingUpdateRequest() {}

    public BookingUpdateRequest(Status status, String mentorResp) {
        this.status = status;
        this.mentorResp = mentorResp;
    }

    // Getters and Setters
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getMentorResp() {
        return mentorResp;
    }

    public void setMentorResp(String mentorResp) {
        this.mentorResp = mentorResp;
    }
}