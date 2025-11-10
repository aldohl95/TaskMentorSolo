package com.taskmentor.taskmentor.service;

import com.taskmentor.taskmentor.dto.StudentProfileRequest;
import com.taskmentor.taskmentor.dto.StudentProfileResponse;
import com.taskmentor.taskmentor.model.StudentProfile;
import com.taskmentor.taskmentor.model.User;
import com.taskmentor.taskmentor.repository.StudentProfileRepository;
import com.taskmentor.taskmentor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentProfileService {

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public StudentProfileResponse createOrUpdateProfile(Long userId, StudentProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StudentProfile profile = studentProfileRepository.findByUser(user)
                .orElse(new StudentProfile());

        profile.setUser(user);
        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setBio(request.getBio());
        profile.setMajor(request.getMajor());
        profile.setGradYear(request.getGradYear());
        profile.setInterests(request.getInterests());

        profile = studentProfileRepository.save(profile);

        return mapToResponse(profile);
    }

    public StudentProfileResponse getProfileByUserId(Long userId) {
        StudentProfile profile = studentProfileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));
        return mapToResponse(profile);
    }

    public StudentProfileResponse getProfileByStudentId(Long studentId) {
        StudentProfile profile = studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));
        return mapToResponse(profile);
    }

    private StudentProfileResponse mapToResponse(StudentProfile profile) {
        return new StudentProfileResponse(
                profile.getStudentId(),
                profile.getUser().getUserId(),
                profile.getFirstName(),
                profile.getLastName(),
                profile.getBio(),
                profile.getMajor(),
                profile.getGradYear(),
                profile.getInterests()
        );
    }
}