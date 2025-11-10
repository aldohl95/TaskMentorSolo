package com.taskmentor.taskmentor.repository;

import com.taskmentor.taskmentor.model.MentorProfile;
import com.taskmentor.taskmentor.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MentorProfileRepository extends JpaRepository<MentorProfile, Long> {

    Optional<MentorProfile> findByUser(User user);

    Optional<MentorProfile> findByUser_UserId(Long userId);

    // Search mentors by various criteria
    @Query("SELECT m FROM MentorProfile m WHERE " +
            "LOWER(m.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(m.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(m.company) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(m.expertise) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(m.industries) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<MentorProfile> searchByKeyword(@Param("keyword") String keyword);

    // Filter by industry
    @Query("SELECT m FROM MentorProfile m WHERE LOWER(m.industries) LIKE LOWER(CONCAT('%', :industry, '%'))")
    List<MentorProfile> findByIndustry(@Param("industry") String industry);

    // Filter by expertise
    @Query("SELECT m FROM MentorProfile m WHERE LOWER(m.expertise) LIKE LOWER(CONCAT('%', :expertise, '%'))")
    List<MentorProfile> findByExpertise(@Param("expertise") String expertise);
}
