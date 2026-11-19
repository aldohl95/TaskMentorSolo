package com.taskmentor.taskmentor.controller;

import com.taskmentor.taskmentor.dto.BookingRequest;
import com.taskmentor.taskmentor.dto.BookingResponse;
import com.taskmentor.taskmentor.dto.BookingUpdateRequest;
import com.taskmentor.taskmentor.security.AuthUser;
import com.taskmentor.taskmentor.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingRequest request,
                                                         @AuthenticationPrincipal AuthUser me) {
        BookingResponse created = bookingService.createBooking(me.userId(), request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.getBookingId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    /** The booking's mentor responds to it; its student may only cancel it. */
    @PutMapping("/{bookingId}")
    public BookingResponse updateBookingStatus(@PathVariable Long bookingId,
                                               @Valid @RequestBody BookingUpdateRequest request,
                                               @AuthenticationPrincipal AuthUser me) {
        return bookingService.updateBookingStatus(bookingId, me, request);
    }

    @GetMapping("/{bookingId}")
    public BookingResponse getBooking(@PathVariable Long bookingId, @AuthenticationPrincipal AuthUser me) {
        return bookingService.getBooking(bookingId, me);
    }

    @GetMapping("/student")
    @PreAuthorize("hasRole('STUDENT')")
    public List<BookingResponse> getMyBookingsAsStudent(@AuthenticationPrincipal AuthUser me) {
        return bookingService.getBookingsForStudent(me.userId());
    }

    @GetMapping("/mentor")
    @PreAuthorize("hasRole('MENTOR')")
    public List<BookingResponse> getMyBookingsAsMentor(@AuthenticationPrincipal AuthUser me) {
        return bookingService.getBookingsForMentor(me.userId());
    }

    @GetMapping("/mentor/pending")
    @PreAuthorize("hasRole('MENTOR')")
    public List<BookingResponse> getPendingBookingsAsMentor(@AuthenticationPrincipal AuthUser me) {
        return bookingService.getPendingBookingsForMentor(me.userId());
    }
}
