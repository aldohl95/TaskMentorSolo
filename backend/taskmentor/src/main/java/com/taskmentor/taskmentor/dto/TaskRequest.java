package com.taskmentor.taskmentor.dto;

import com.taskmentor.taskmentor.enums.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

public class TaskRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be at most 200 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 2000, message = "Description must be at most 2000 characters")
    private String description;

    @NotNull(message = "Duration is required")
    @Min(value = 15, message = "Duration must be at least 15 minutes")
    @Max(value = 180, message = "Duration must be at most 180 minutes")
    private Integer duration;

    @NotNull(message = "Category is required")
    private Category category;

    // Constructors
    public TaskRequest() {}

    public TaskRequest(String title, String description, Integer duration, Category category) {
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.category = category;
    }

    // Getters and Setters
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
}