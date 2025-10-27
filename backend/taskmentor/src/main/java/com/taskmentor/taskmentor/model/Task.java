package com.taskmentor.taskmentor.model;

import com.taskmentor.taskmentor.enums.Category;
import jakarta.persistence.*;
import org.springframework.boot.autoconfigure.web.WebProperties;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_seq")
    @SequenceGenerator(name = "task_seq", sequenceName = "tasks_task_id_seq", allocationSize = 1)
    @Column(name = "task_id", nullable = false, updatable = false)
    private Long taskId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id")
    private MentorProfile mentor;

    private String title;
    private String description;
    private Integer duration;

    @Enumerated(EnumType.STRING)
    private Category category;

    public Task() {

    }

    public Task(Long taskId, MentorProfile mentor, String title, String description, Integer duration,
                 Category category) {
        this.taskId = taskId;
        this.mentor = mentor;
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.category = category;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public MentorProfile getMentor() {
        return mentor;
    }

    public void setMentor(MentorProfile mentor) {
        this.mentor = mentor;
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

    public Integer getDurationMax() {
        return duration;
    }


    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskId=" + taskId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", duration=" + duration + " min" +
                ", category='" + category + '\'' +
                '}';
    }

}
