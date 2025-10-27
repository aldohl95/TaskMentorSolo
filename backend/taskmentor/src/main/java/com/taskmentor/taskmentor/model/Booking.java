package com.taskmentor.taskmentor.model;


import com.taskmentor.taskmentor.enums.Status;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @Column(name = "booking_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "booking_seq")
    @SequenceGenerator(name = "booking_seq", sequenceName = "bookings_booking_seq", allocationSize = 1)
    private Long bookingId;

    @ManyToOne
    @JoinColumn(name ="student_id", nullable = false )
    private StudentProfile student;

    @ManyToOne
    @JoinColumn(name = "mentor_id", nullable = false)
    private MentorProfile mentor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @Column(name = "proposed_date")
    private LocalDateTime proposedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "student_msg")
    private String studentMsg;

    @Column(name = "mentor_resp")
    private String mentorResp;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Booking(){

    }

    public Booking(Long bookingId, StudentProfile student, MentorProfile mentor, Task task,
                   LocalDateTime proposedDate, Status status, String studentMsg, String mentorResp,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
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

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public StudentProfile getStudent() {
        return student;
    }

    public void setStudent(StudentProfile student) {
        this.student = student;
    }

    public MentorProfile getMentor() {
        return mentor;
    }

    public void setMentor(MentorProfile mentor) {
        this.mentor = mentor;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
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

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
