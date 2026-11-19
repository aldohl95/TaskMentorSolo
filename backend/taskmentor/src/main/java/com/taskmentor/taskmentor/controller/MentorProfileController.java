package com.taskmentor.taskmentor.controller;

import com.taskmentor.taskmentor.dto.MentorProfileRequest;
import com.taskmentor.taskmentor.dto.MentorProfileResponse;
import com.taskmentor.taskmentor.dto.SaveResult;
import com.taskmentor.taskmentor.security.AuthUser;
import com.taskmentor.taskmentor.service.MentorProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mentors")
public class MentorProfileController {

    private final MentorProfileService mentorProfileService;

    public MentorProfileController(MentorProfileService mentorProfileService) {
        this.mentorProfileService = mentorProfileService;
    }

    /** Idempotent save of the caller's own profile: 201 the first time, 200 afterwards. */
    @PutMapping("/profile")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<MentorProfileResponse> saveMyProfile(@Valid @RequestBody MentorProfileRequest request,
                                                               @AuthenticationPrincipal AuthUser me) {
        SaveResult<MentorProfileResponse> result = mentorProfileService.createOrUpdateProfile(me.userId(), request);
        return ResponseEntity.status(result.created() ? HttpStatus.CREATED : HttpStatus.OK).body(result.body());
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('MENTOR')")
    public MentorProfileResponse getMyProfile(@AuthenticationPrincipal AuthUser me) {
        return mentorProfileService.getProfileByUserId(me.userId());
    }

    @GetMapping("/{mentorId}/profile")
    public MentorProfileResponse getMentorProfile(@PathVariable Long mentorId) {
        return mentorProfileService.getProfileByMentorId(mentorId);
    }

    @GetMapping("/search")
    public List<MentorProfileResponse> searchMentors(@RequestParam(required = false) String keyword,
                                                     @RequestParam(required = false) String industry,
                                                     @RequestParam(required = false) String expertise) {
        if (keyword != null && !keyword.isEmpty()) {
            return mentorProfileService.searchMentors(keyword);
        } else if (industry != null && !industry.isEmpty()) {
            return mentorProfileService.filterByIndustry(industry);
        } else if (expertise != null && !expertise.isEmpty()) {
            return mentorProfileService.filterByExpertise(expertise);
        }
        return mentorProfileService.getAllMentors();
    }
}
