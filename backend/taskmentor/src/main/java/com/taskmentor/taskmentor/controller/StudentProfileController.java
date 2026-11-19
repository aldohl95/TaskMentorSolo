package com.taskmentor.taskmentor.controller;

import com.taskmentor.taskmentor.dto.SaveResult;
import com.taskmentor.taskmentor.dto.StudentProfileRequest;
import com.taskmentor.taskmentor.dto.StudentProfileResponse;
import com.taskmentor.taskmentor.security.AuthUser;
import com.taskmentor.taskmentor.service.StudentProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
public class StudentProfileController {

    private final StudentProfileService studentProfileService;

    public StudentProfileController(StudentProfileService studentProfileService) {
        this.studentProfileService = studentProfileService;
    }

    /** Idempotent save of the caller's own profile: 201 the first time, 200 afterwards. */
    @PutMapping("/profile")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudentProfileResponse> saveMyProfile(@Valid @RequestBody StudentProfileRequest request,
                                                                @AuthenticationPrincipal AuthUser me) {
        SaveResult<StudentProfileResponse> result = studentProfileService.createOrUpdateProfile(me.userId(), request);
        return ResponseEntity.status(result.created() ? HttpStatus.CREATED : HttpStatus.OK).body(result.body());
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('STUDENT')")
    public StudentProfileResponse getMyProfile(@AuthenticationPrincipal AuthUser me) {
        return studentProfileService.getProfileByUserId(me.userId());
    }

    @GetMapping("/{studentId}/profile")
    public StudentProfileResponse getStudentProfile(@PathVariable Long studentId,
                                                    @AuthenticationPrincipal AuthUser me) {
        return studentProfileService.getProfileByStudentId(studentId, me);
    }
}
