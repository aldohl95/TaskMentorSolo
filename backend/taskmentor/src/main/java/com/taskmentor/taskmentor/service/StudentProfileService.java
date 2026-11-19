package com.taskmentor.taskmentor.service;

import com.taskmentor.taskmentor.dto.SaveResult;
import com.taskmentor.taskmentor.dto.StudentProfileRequest;
import com.taskmentor.taskmentor.dto.StudentProfileResponse;
import com.taskmentor.taskmentor.enums.AccountType;
import com.taskmentor.taskmentor.exception.NotFoundException;
import com.taskmentor.taskmentor.model.StudentProfile;
import com.taskmentor.taskmentor.model.User;
import com.taskmentor.taskmentor.repository.BookingRepository;
import com.taskmentor.taskmentor.repository.StudentProfileRepository;
import com.taskmentor.taskmentor.repository.UserRepository;
import com.taskmentor.taskmentor.security.AuthUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class StudentProfileService {

    private final StudentProfileRepository studentProfileRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    public StudentProfileService(StudentProfileRepository studentProfileRepository,
                                 UserRepository userRepository,
                                 BookingRepository bookingRepository) {
        this.studentProfileRepository = studentProfileRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public SaveResult<StudentProfileResponse> createOrUpdateProfile(Long userId, StudentProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Optional<StudentProfile> existing = studentProfileRepository.findByUser(user);
        StudentProfile profile = existing.orElseGet(StudentProfile::new);

        profile.setUser(user);
        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setBio(request.getBio());
        profile.setMajor(request.getMajor());
        profile.setGradYear(request.getGradYear());
        profile.setInterests(request.getInterests());

        profile = studentProfileRepository.save(profile);

        return new SaveResult<>(mapToResponse(profile), existing.isEmpty());
    }

    public StudentProfileResponse getProfileByUserId(Long userId) {
        StudentProfile profile = studentProfileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new NotFoundException("Student profile not found"));
        return mapToResponse(profile);
    }

    /**
     * A student profile is private. Only its owner, or a mentor who has been booked by that student,
     * may read it; everyone else gets 404 so the existence of the profile is not revealed.
     */
    @Transactional(readOnly = true)
    public StudentProfileResponse getProfileByStudentId(Long studentId, AuthUser viewer) {
        StudentProfile profile = studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student profile not found"));

        boolean isOwner = profile.getUser().getUserId().equals(viewer.userId());
        boolean isBookedMentor = viewer.role() == AccountType.MENTOR
                && bookingRepository.existsByMentor_User_UserIdAndStudent_StudentId(viewer.userId(), studentId);

        if (!isOwner && !isBookedMentor) {
            throw new NotFoundException("Student profile not found");
        }
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
