package com.taskmentor.taskmentor.service;

import com.taskmentor.taskmentor.dto.*;
import com.taskmentor.taskmentor.enums.Status;
import com.taskmentor.taskmentor.exception.BadRequestException;
import com.taskmentor.taskmentor.exception.ConflictException;
import com.taskmentor.taskmentor.exception.ForbiddenException;
import com.taskmentor.taskmentor.exception.NotFoundException;
import com.taskmentor.taskmentor.model.Booking;
import com.taskmentor.taskmentor.model.MentorProfile;
import com.taskmentor.taskmentor.model.StudentProfile;
import com.taskmentor.taskmentor.model.Task;
import com.taskmentor.taskmentor.repository.BookingRepository;
import com.taskmentor.taskmentor.repository.MentorProfileRepository;
import com.taskmentor.taskmentor.repository.StudentProfileRepository;
import com.taskmentor.taskmentor.repository.TaskRepository;
import com.taskmentor.taskmentor.security.AuthUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final MentorProfileRepository mentorProfileRepository;
    private final TaskRepository taskRepository;

    public BookingService(BookingRepository bookingRepository,
                          StudentProfileRepository studentProfileRepository,
                          MentorProfileRepository mentorProfileRepository,
                          TaskRepository taskRepository) {
        this.bookingRepository = bookingRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.mentorProfileRepository = mentorProfileRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional
    public BookingResponse createBooking(Long userId, BookingRequest request) {
        StudentProfile student = studentProfileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ConflictException("Complete your student profile before booking a session"));

        MentorProfile mentor = mentorProfileRepository.findById(request.getMentorId())
                .orElseThrow(() -> new NotFoundException("Mentor not found"));

        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new NotFoundException("Task not found"));

        if (!task.getMentor().getMentorId().equals(mentor.getMentorId())) {
            throw new BadRequestException("That task is not offered by this mentor");
        }

        // Check for double booking
        List<Booking> conflicts = bookingRepository.findConflictingBookings(mentor, request.getProposedDate());
        if (!conflicts.isEmpty()) {
            throw new ConflictException("This time slot is already booked");
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

    /**
     * The booking's student may only cancel it; its mentor may set any status. Anyone else gets 404
     * so the existence of the booking is not revealed. A full state machine comes in Phase 2.
     */
    @Transactional
    public BookingResponse updateBookingStatus(Long bookingId, AuthUser actor, BookingUpdateRequest request) {
        Booking booking = findVisibleBooking(bookingId, actor);

        boolean isMentor = booking.getMentor().getUser().getUserId().equals(actor.userId());
        if (!isMentor && request.getStatus() != Status.CANCELLED) {
            throw new ForbiddenException("Students can only cancel a booking");
        }

        // Check for double booking when accepting
        if (request.getStatus() == Status.ACCEPTED) {
            List<Booking> conflicts = bookingRepository.findConflictingBookings(
                            booking.getMentor(), booking.getProposedDate()).stream()
                    .filter(b -> !b.getBookingId().equals(bookingId))
                    .collect(Collectors.toList());
            if (!conflicts.isEmpty()) {
                throw new ConflictException("This time slot is already booked");
            }
        }

        booking.setStatus(request.getStatus());
        if (isMentor) {
            booking.setMentorResp(request.getMentorResp());
        }

        return mapToResponse(bookingRepository.save(booking));
    }

    /** Visible only to the booking's own student and mentor. */
    @Transactional(readOnly = true)
    public BookingResponse getBooking(Long bookingId, AuthUser viewer) {
        return mapToResponse(findVisibleBooking(bookingId, viewer));
    }

    private Booking findVisibleBooking(Long bookingId, AuthUser viewer) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        boolean isStudent = booking.getStudent().getUser().getUserId().equals(viewer.userId());
        boolean isMentor = booking.getMentor().getUser().getUserId().equals(viewer.userId());
        if (!isStudent && !isMentor) {
            throw new NotFoundException("Booking not found");
        }
        return booking;
    }

    /** A user without a profile cannot have bookings yet, so the list is simply empty. */
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsForStudent(Long userId) {
        return studentProfileRepository.findByUser_UserId(userId)
                .map(student -> toResponses(bookingRepository.findByStudent(student)))
                .orElseGet(List::of);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsForMentor(Long userId) {
        return mentorProfileRepository.findByUser_UserId(userId)
                .map(mentor -> toResponses(bookingRepository.findByMentor(mentor)))
                .orElseGet(List::of);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getPendingBookingsForMentor(Long userId) {
        return mentorProfileRepository.findByUser_UserId(userId)
                .map(mentor -> toResponses(bookingRepository.findByMentorAndStatus(mentor, Status.PENDING)))
                .orElseGet(List::of);
    }

    private List<BookingResponse> toResponses(List<Booking> bookings) {
        return bookings.stream().map(this::mapToResponse).collect(Collectors.toList());
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
