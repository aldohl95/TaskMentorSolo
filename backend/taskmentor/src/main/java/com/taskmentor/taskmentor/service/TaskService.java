package com.taskmentor.taskmentor.service;

import com.taskmentor.taskmentor.dto.TaskRequest;
import com.taskmentor.taskmentor.dto.TaskResponse;
import com.taskmentor.taskmentor.enums.Category;
import com.taskmentor.taskmentor.model.MentorProfile;
import com.taskmentor.taskmentor.model.Task;
import com.taskmentor.taskmentor.repository.MentorProfileRepository;
import com.taskmentor.taskmentor.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private MentorProfileRepository mentorProfileRepository;

    @Transactional
    public TaskResponse createTask(Long userId, TaskRequest request) {
        MentorProfile mentor = mentorProfileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Mentor profile not found"));

        Task task = new Task();
        task.setMentor(mentor);
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDuration(request.getDuration());
        task.setCategory(request.getCategory());

        task = taskRepository.save(task);

        return mapToResponse(task);
    }

    @Transactional
    public TaskResponse updateTask(Long taskId, Long userId, TaskRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Verify the task belongs to the requesting mentor
        if (!task.getMentor().getUser().getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized to update this task");
        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDuration(request.getDuration());
        task.setCategory(request.getCategory());

        task = taskRepository.save(task);

        return mapToResponse(task);
    }

    @Transactional
    public void deleteTask(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Verify the task belongs to the requesting mentor
        if (!task.getMentor().getUser().getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this task");
        }

        // TODO: Add check to prevent deletion if there are upcoming bookings

        taskRepository.delete(task);
    }

    public TaskResponse getTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return mapToResponse(task);
    }

    public List<TaskResponse> getTasksByMentor(Long mentorId) {
        return taskRepository.findByMentor_MentorId(mentorId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<TaskResponse> getTasksByCategory(Category category) {
        return taskRepository.findByCategory(category).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private TaskResponse mapToResponse(Task task) {
        return new TaskResponse(
                task.getTaskId(),
                task.getTitle(),
                task.getDescription(),
                task.getDuration(),
                task.getCategory(),
                task.getMentor().getMentorId()
        );
    }
}
