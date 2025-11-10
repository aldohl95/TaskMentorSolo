package com.taskmentor.taskmentor.service;

import com.taskmentor.taskmentor.dto.MentorProfileRequest;
import com.taskmentor.taskmentor.dto.MentorProfileResponse;
import com.taskmentor.taskmentor.dto.TaskResponse;
import com.taskmentor.taskmentor.model.MentorProfile;
import com.taskmentor.taskmentor.model.Task;
import com.taskmentor.taskmentor.model.User;
import com.taskmentor.taskmentor.repository.MentorProfileRepository;
import com.taskmentor.taskmentor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MentorProfileService {

    @Autowired
    private MentorProfileRepository mentorProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public MentorProfileResponse createOrUpdateProfile(Long userId, MentorProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        MentorProfile profile = mentorProfileRepository.findByUser(user)
                .orElse(new MentorProfile());

        profile.setUser(user);
        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setBio(request.getBio());
        profile.setRoleTitle(request.getRoleTitle());
        profile.setCompany(request.getCompany());
        profile.setYrsExp(request.getYrsExp());
        profile.setIndustries(request.getIndustries());
        profile.setExpertise(request.getExpertise());
        profile.setPhotoUrl(request.getPhotoUrl());

        profile = mentorProfileRepository.save(profile);

        return mapToResponse(profile);
    }

    public MentorProfileResponse getProfileByUserId(Long userId) {
        MentorProfile profile = mentorProfileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Mentor profile not found"));
        return mapToResponse(profile);
    }

    public MentorProfileResponse getProfileByMentorId(Long mentorId) {
        MentorProfile profile = mentorProfileRepository.findById(mentorId)
                .orElseThrow(() -> new RuntimeException("Mentor profile not found"));
        return mapToResponse(profile);
    }

    public List<MentorProfileResponse> getAllMentors() {
        return mentorProfileRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<MentorProfileResponse> searchMentors(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllMentors();
        }
        return mentorProfileRepository.searchByKeyword(keyword).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<MentorProfileResponse> filterByIndustry(String industry) {
        return mentorProfileRepository.findByIndustry(industry).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<MentorProfileResponse> filterByExpertise(String expertise) {
        return mentorProfileRepository.findByExpertise(expertise).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private MentorProfileResponse mapToResponse(MentorProfile profile) {
        List<TaskResponse> tasks = profile.getTasks().stream()
                .map(this::mapTaskToResponse)
                .collect(Collectors.toList());

        return new MentorProfileResponse(
                profile.getMentorId(),
                profile.getUser().getUserId(),
                profile.getFirstName(),
                profile.getLastName(),
                profile.getBio(),
                profile.getRoleTitle(),
                profile.getCompany(),
                profile.getYrsExp(),
                profile.getIndustries(),
                profile.getExpertise(),
                profile.getPhotoUrl(),
                tasks
        );
    }

    private TaskResponse mapTaskToResponse(Task task) {
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