package com.taskmentor.taskmentor.controller;

import com.taskmentor.taskmentor.dto.TaskRequest;
import com.taskmentor.taskmentor.dto.TaskResponse;
import com.taskmentor.taskmentor.enums.Category;
import com.taskmentor.taskmentor.security.AuthUser;
import com.taskmentor.taskmentor.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request,
                                                   @AuthenticationPrincipal AuthUser me) {
        TaskResponse created = taskService.createTask(me.userId(), request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.getTaskId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{taskId}")
    @PreAuthorize("hasRole('MENTOR')")
    public TaskResponse updateTask(@PathVariable Long taskId,
                                   @Valid @RequestBody TaskRequest request,
                                   @AuthenticationPrincipal AuthUser me) {
        return taskService.updateTask(taskId, me.userId(), request);
    }

    @DeleteMapping("/{taskId}")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId, @AuthenticationPrincipal AuthUser me) {
        taskService.deleteTask(taskId, me.userId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{taskId}")
    public TaskResponse getTask(@PathVariable Long taskId) {
        return taskService.getTask(taskId);
    }

    @GetMapping("/mentor/{mentorId}")
    public List<TaskResponse> getTasksByMentor(@PathVariable Long mentorId) {
        return taskService.getTasksByMentor(mentorId);
    }

    @GetMapping("/category/{category}")
    public List<TaskResponse> getTasksByCategory(@PathVariable Category category) {
        return taskService.getTasksByCategory(category);
    }

    @GetMapping
    public List<TaskResponse> getAllTasks() {
        return taskService.getAllTasks();
    }
}
