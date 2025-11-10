package com.taskmentor.taskmentor.controller;

import com.taskmentor.taskmentor.dto.MentorProfileRequest;
import com.taskmentor.taskmentor.dto.MentorProfileResponse;
import com.taskmentor.taskmentor.service.MentorProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mentors")
@CrossOrigin(origins = "http://localhost:5173")
public class MentorProfileController {

    @Autowired
    private MentorProfileService mentorProfileService;

    @Autowired
    private com.taskmentor.taskmentor.repository.UserRepository userRepository;

    @PostMapping("/profile")
    public ResponseEntity<?> createOrUpdateProfile(@Valid @RequestBody MentorProfileRequest request,
                                                   Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Long userId = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"))
                    .getUserId();

            MentorProfileResponse response = mentorProfileService.createOrUpdateProfile(userId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getMyProfile(Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Long userId = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"))
                    .getUserId();

            MentorProfileResponse response = mentorProfileService.getProfileByUserId(userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{mentorId}/profile")
    public ResponseEntity<?> getMentorProfile(@PathVariable Long mentorId) {
        try {
            MentorProfileResponse response = mentorProfileService.getProfileByMentorId(mentorId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchMentors(@RequestParam(required = false) String keyword,
                                           @RequestParam(required = false) String industry,
                                           @RequestParam(required = false) String expertise) {
        try {
            List<MentorProfileResponse> mentors;

            if (keyword != null && !keyword.isEmpty()) {
                mentors = mentorProfileService.searchMentors(keyword);
            } else if (industry != null && !industry.isEmpty()) {
                mentors = mentorProfileService.filterByIndustry(industry);
            } else if (expertise != null && !expertise.isEmpty()) {
                mentors = mentorProfileService.filterByExpertise(expertise);
            } else {
                mentors = mentorProfileService.getAllMentors();
            }

            return ResponseEntity.ok(mentors);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}