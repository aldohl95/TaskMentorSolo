package com.taskmentor.taskmentor.dto;

import com.taskmentor.taskmentor.enums.Category;

public class TaskResponse {
    private Long taskId;
    private String title;
    private String description;
    private Integer duration;
    private Category category;
    private Long mentorId;

    // Constructors
    public TaskResponse() {}

    public TaskResponse(Long taskId, String title, String description, Integer duration,
                        Category category, Long mentorId) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.category = category;
        this.mentorId = mentorId;
    }

    // Getters and Setters
    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Long getMentorId() {
        return mentorId;
    }

    public void setMentorId(Long mentorId) {
        this.mentorId = mentorId;
    }
}