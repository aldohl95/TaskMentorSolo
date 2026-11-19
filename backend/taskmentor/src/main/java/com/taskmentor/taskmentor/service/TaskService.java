package com.taskmentor.taskmentor.service;

import com.taskmentor.taskmentor.dto.TaskRequest;
import com.taskmentor.taskmentor.dto.TaskResponse;
import com.taskmentor.taskmentor.enums.Category;
import com.taskmentor.taskmentor.exception.ConflictException;
import com.taskmentor.taskmentor.exception.ForbiddenException;
import com.taskmentor.taskmentor.exception.NotFoundException;
import com.taskmentor.taskmentor.model.MentorProfile;
import com.taskmentor.taskmentor.model.Task;
import com.taskmentor.taskmentor.repository.MentorProfileRepository;
import com.taskmentor.taskmentor.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final MentorProfileRepository mentorProfileRepository;

    public TaskService(TaskRepository taskRepository, MentorProfileRepository mentorProfileRepository) {
        this.taskRepository = taskRepository;
        this.mentorProfileRepository = mentorProfileRepository;
    }

    @Transactional
    public TaskResponse createTask(Long userId, TaskRequest request) {
        MentorProfile mentor = mentorProfileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ConflictException("Complete your mentor profile before creating tasks"));

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
                .orElseThrow(() -> new NotFoundException("Task not found"));

        // Verify the task belongs to the requesting mentor
        if (!task.getMentor().getUser().getUserId().equals(userId)) {
            throw new ForbiddenException("You can only edit your own tasks");
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
                .orElseThrow(() -> new NotFoundException("Task not found"));

        // Verify the task belongs to the requesting mentor
        if (!task.getMentor().getUser().getUserId().equals(userId)) {
            throw new ForbiddenException("You can only delete your own tasks");
        }

        // TODO: Add check to prevent deletion if there are upcoming bookings

        taskRepository.delete(task);
    }

    public TaskResponse getTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task not found"));
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
