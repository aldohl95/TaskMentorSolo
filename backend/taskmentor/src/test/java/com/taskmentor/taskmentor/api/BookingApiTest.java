package com.taskmentor.taskmentor.api;

import com.taskmentor.taskmentor.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.hamcrest.Matchers.endsWith;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BookingApiTest extends IntegrationTest {

    private Session student;
    private Mentor mentor;
    private long taskId;

    @BeforeEach
    void fixtures() throws Exception {
        student = studentWithProfile();
        mentor = mentorWithProfile();
        taskId = createTask(mentor.session());
    }

    private long book(String slot) throws Exception {
        return body(doPost("/api/bookings", student, bookingRequest(mentor.mentorId(), taskId, slot))
                .andExpect(status().isCreated())).get("bookingId").asLong();
    }

    @Test
    void createBooking_returns201WithLocationAndStartsPending() throws Exception {
        var result = doPost("/api/bookings", student, bookingRequest(mentor.mentorId(), taskId, uniqueFutureSlot()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"));

        long bookingId = body(result).get("bookingId").asLong();
        result.andExpect(header().string("Location", endsWith("/api/bookings/" + bookingId)));
    }

    @Test
    void createBooking_asAMentor_returns403() throws Exception {
        doPost("/api/bookings", mentor.session(), bookingRequest(mentor.mentorId(), taskId, uniqueFutureSlot()))
                .andExpect(status().isForbidden());
    }

    @Test
    void createBooking_beforeSavingAStudentProfile_returns409() throws Exception {
        doPost("/api/bookings", register("STUDENT"), bookingRequest(mentor.mentorId(), taskId, uniqueFutureSlot()))
                .andExpect(status().isConflict());
    }

    @Test
    void createBooking_inThePast_returns400() throws Exception {
        String past = LocalDateTime.now().minusDays(1).withNano(0).toString();
        doPost("/api/bookings", student, bookingRequest(mentor.mentorId(), taskId, past))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.proposedDate").value("Proposed date must be in the future"));
    }

    @Test
    void createBooking_forATaskThatBelongsToAnotherMentor_returns400() throws Exception {
        Mentor other = mentorWithProfile();
        doPost("/api/bookings", student, bookingRequest(other.mentorId(), taskId, uniqueFutureSlot()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("That task is not offered by this mentor"));
    }

    @Test
    void createBooking_forUnknownTaskOrMentor_returns404() throws Exception {
        doPost("/api/bookings", student, bookingRequest(mentor.mentorId(), 999999, uniqueFutureSlot()))
                .andExpect(status().isNotFound());
        doPost("/api/bookings", student, bookingRequest(999999, taskId, uniqueFutureSlot()))
                .andExpect(status().isNotFound());
    }

    @Test
    void createBooking_missingFields_returns400() throws Exception {
        doPost("/api/bookings", student, Map.of("studentMsg", "hi"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.mentorId").exists())
                .andExpect(jsonPath("$.errors.taskId").exists())
                .andExpect(jsonPath("$.errors.proposedDate").exists());
    }

    @Test
    void acceptingASecondBookingForTheSameSlot_returns409() throws Exception {
        String slot = uniqueFutureSlot();
        long first = book(slot);
        long second = book(slot);

        doPut("/api/bookings/" + first, mentor.session(), Map.of("status", "ACCEPTED")).andExpect(status().isOk());
        doPut("/api/bookings/" + second, mentor.session(), Map.of("status", "ACCEPTED"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("This time slot is already booked"));
    }

    // ---- who may see and change a booking ---------------------------------------------------

    @Test
    void getBooking_isVisibleToItsStudentAndMentorOnly() throws Exception {
        String url = "/api/bookings/" + book(uniqueFutureSlot());

        doGet(url, student).andExpect(status().isOk());
        doGet(url, mentor.session()).andExpect(status().isOk());

        // Regression (IDOR): any logged-in user used to be able to read any booking by its id.
        // Other users get 404 so the booking's existence is not revealed.
        doGet(url, studentWithProfile()).andExpect(status().isNotFound());
        doGet(url, mentorWithProfile().session()).andExpect(status().isNotFound());
        doGet(url, null).andExpect(status().isUnauthorized());
    }

    @Test
    void updateBooking_byAnUnrelatedUser_returns404() throws Exception {
        String url = "/api/bookings/" + book(uniqueFutureSlot());

        doPut(url, mentorWithProfile().session(), Map.of("status", "ACCEPTED")).andExpect(status().isNotFound());
        doPut(url, studentWithProfile(), Map.of("status", "CANCELLED")).andExpect(status().isNotFound());
    }

    @Test
    void mentor_canAcceptAndRespond() throws Exception {
        String url = "/api/bookings/" + book(uniqueFutureSlot());

        doPut(url, mentor.session(), Map.of("status", "ACCEPTED", "mentorResp", "See you then"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"))
                .andExpect(jsonPath("$.mentorResp").value("See you then"));
    }

    @Test
    void student_canCancelTheirBooking() throws Exception {
        String url = "/api/bookings/" + book(uniqueFutureSlot());

        doPut(url, student, Map.of("status", "CANCELLED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void student_cannotAcceptOrCompleteTheirOwnBooking() throws Exception {
        String url = "/api/bookings/" + book(uniqueFutureSlot());

        doPut(url, student, Map.of("status", "ACCEPTED")).andExpect(status().isForbidden());
        doPut(url, student, Map.of("status", "COMPLETED")).andExpect(status().isForbidden());
    }

    @Test
    void student_cannotWriteAMentorResponse() throws Exception {
        String url = "/api/bookings/" + book(uniqueFutureSlot());

        doPut(url, student, Map.of("status", "CANCELLED", "mentorResp", "forged reply")).andExpect(status().isOk())
                .andExpect(jsonPath("$.mentorResp").doesNotExist());
    }

    // ---- lists -------------------------------------------------------------------------------

    @Test
    void bookingLists_areScopedToTheCallersRole() throws Exception {
        book(uniqueFutureSlot());

        doGet("/api/bookings/student", student).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        doGet("/api/bookings/mentor", mentor.session()).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        doGet("/api/bookings/mentor/pending", mentor.session()).andExpect(status().isOk());

        doGet("/api/bookings/student", mentor.session()).andExpect(status().isForbidden());
        doGet("/api/bookings/mentor", student).andExpect(status().isForbidden());
        doGet("/api/bookings/mentor/pending", student).andExpect(status().isForbidden());
    }

    @Test
    void unknownBooking_returns404() throws Exception {
        doGet("/api/bookings/999999", student).andExpect(status().isNotFound());
    }
}
