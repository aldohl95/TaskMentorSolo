package com.taskmentor.taskmentor.service;

import com.taskmentor.taskmentor.dto.*;
import com.taskmentor.taskmentor.enums.Status;
import com.taskmentor.taskmentor.model.Booking;
import com.taskmentor.taskmentor.model.MentorProfile;
import com.taskmentor.taskmentor.model.StudentProfile;
import com.taskmentor.taskmentor.model.Task;
import com.taskmentor.taskmentor.repository.BookingRepository;
import com.taskmentor.taskmentor.repository.MentorProfileRepository;
import com.taskmentor.taskmentor.repository.StudentProfileRepository;
import com.taskmentor.taskmentor.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private MentorProfileRepository mentorProfileRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Transactional
    public BookingResponse createBooking(Long userId, BookingRequest request) {
        StudentProfile student = studentProfileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        MentorProfile mentor = mentorProfileRepository.findById(request.getMentorId())
                .orElseThrow(() -> new RuntimeException("Mentor not found"));

        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Check for double booking
        List<Booking> conflicts = bookingRepository.findConflictingBookings(mentor, request.getProposedDate());
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("This time slot is already booked");
        }

        Booking booking = new Booking();
        booking.setStudent(student);
        booking.setMentor(mentor);
        booking.setTask(task);
        booking.setProposedDate(request.getProposedDate());
        booking.setStudentMsg(request.getStudentMsg());
        booking.setStatus(Status.PENDING);

        booking = bookingRepository.save(booking);

        return mapToResponse(booking);
    }

    @Transactional
    public BookingResponse updateBookingStatus(Long bookingId, Long userId, BookingUpdateRequest request) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Verify the booking belongs to the requesting mentor
        if (!booking.getMentor().getUser().getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized to update this booking");
        }

        // Check for double booking when accepting
        if (request.getStatus() == Status.ACCEPTED) {
            List<Booking> conflicts = bookingRepository.findConflictingBookings(
                    booking.getMentor(), booking.getProposedDate());
            conflicts = conflicts.stream()
                    .filter(b -> !b.getBookingId().equals(bookingId))
                    .collect(Collectors.toList());
            if (!conflicts.isEmpty()) {
                throw new RuntimeException("This time slot is already booked");
            }
        }

        booking.setStatus(request.getStatus());
        booking.setMentorResp(request.getMentorResp());

        booking = bookingRepository.save(booking);

        return mapToResponse(booking);
    }

    public BookingResponse getBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        return mapToResponse(booking);
    }

    public List<BookingResponse> getBookingsForStudent(Long userId) {
        StudentProfile student = studentProfileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));
        return bookingRepository.findByStudent(student).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<BookingResponse> getBookingsForMentor(Long userId) {
        MentorProfile mentor = mentorProfileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Mentor profile not found"));
        return bookingRepository.findByMentor(mentor).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<BookingResponse> getPendingBookingsForMentor(Long userId) {
        MentorProfile mentor = mentorProfileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Mentor profile not found"));
        return bookingRepository.findByMentorAndStatus(mentor, Status.PENDING).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private BookingResponse mapToResponse(Booking booking) {
        StudentProfileResponse studentResponse = mapStudentToResponse(booking.getStudent());
        MentorProfileResponse mentorResponse = mapMentorToResponse(booking.getMentor());
        TaskResponse taskResponse = mapTaskToResponse(booking.getTask());

        return new BookingResponse(
                booking.getBookingId(),
                studentResponse,
                mentorResponse,
                taskResponse,
                booking.getProposedDate(),
                booking.getStatus(),
                booking.getStudentMsg(),
                booking.getMentorResp(),
                booking.getCreatedAt(),
                booking.getUpdatedAt()
        );
    }

    private StudentProfileResponse mapStudentToResponse(StudentProfile student) {
        return new StudentProfileResponse(
                student.getStudentId(),
                student.getUser().getUserId(),
                student.getFirstName(),
                student.getLastName(),
                student.getBio(),
                student.getMajor(),
                student.getGradYear(),
                student.getInterests()
        );
    }

    private MentorProfileResponse mapMentorToResponse(MentorProfile mentor) {
        return new MentorProfileResponse(
                mentor.getMentorId(),
                mentor.getUser().getUserId(),
                mentor.getFirstName(),
                mentor.getLastName(),
                mentor.getBio(),
                mentor.getRoleTitle(),
                mentor.getCompany(),
                mentor.getYrsExp(),
                mentor.getIndustries(),
                mentor.getExpertise(),
                mentor.getPhotoUrl(),
                null // Don't include tasks in booking response
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






