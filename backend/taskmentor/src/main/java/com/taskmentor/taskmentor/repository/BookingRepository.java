package com.taskmentor.taskmentor.repository;

import com.taskmentor.taskmentor.enums.Status;
import com.taskmentor.taskmentor.model.Booking;
import com.taskmentor.taskmentor.model.MentorProfile;
import com.taskmentor.taskmentor.model.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByStudent(StudentProfile student);

    List<Booking> findByMentor(MentorProfile mentor);

    List<Booking> findByStatus(Status status);

    List<Booking> findByStudentAndStatus(StudentProfile student, Status status);

    List<Booking> findByMentorAndStatus(MentorProfile mentor, Status status);

    // Check for double booking
    @Query("SELECT b FROM Booking b WHERE b.mentor = :mentor AND b.status = 'ACCEPTED' " +
            "AND b.proposedDate = :proposedDate")
    List<Booking> findConflictingBookings(@Param("mentor") MentorProfile mentor,
                                          @Param("proposedDate") LocalDateTime proposedDate);

    // Get all bookings for a mentor within a date range
    @Query("SELECT b FROM Booking b WHERE b.mentor = :mentor " +
            "AND b.proposedDate BETWEEN :startDate AND :endDate " +
            "ORDER BY b.proposedDate ASC")
    List<Booking> findByMentorAndDateRange(@Param("mentor") MentorProfile mentor,
                                           @Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);
}
