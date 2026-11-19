package com.taskmentor.taskmentor.api;

import com.taskmentor.taskmentor.IntegrationTest;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Regression: the request validation allowed up to 2000 characters, but Hibernate had created the
 * columns as VARCHAR(255), so a 300-character bio passed validation and then failed in the database.
 * Every text field is sent at exactly its maximum allowed length, so the database must be at least as
 * wide as the validation. If someone raises a {@code @Size} without a migration, this test fails.
 */
class FieldLengthApiTest extends IntegrationTest {

    private static String chars(int n) {
        return "x".repeat(n);
    }

    @Test
    void studentProfile_acceptsEveryFieldAtItsMaximumLength() throws Exception {
        Map<String, Object> profile = new HashMap<>();
        profile.put("firstName", chars(100));
        profile.put("lastName", chars(100));
        profile.put("bio", chars(2000));
        profile.put("major", chars(100));
        profile.put("gradYear", 2100);
        profile.put("interests", chars(1000));

        doPut("/api/students/profile", register("STUDENT"), profile).andExpect(status().isCreated());
    }

    @Test
    void mentorProfile_acceptsEveryFieldAtItsMaximumLength() throws Exception {
        Map<String, Object> profile = new HashMap<>();
        profile.put("firstName", chars(100));
        profile.put("lastName", chars(100));
        profile.put("bio", chars(2000));
        profile.put("roleTitle", chars(100));
        profile.put("company", chars(100));
        profile.put("yrsExp", chars(50));
        profile.put("industries", chars(1000));
        profile.put("expertise", chars(1000));
        profile.put("photoUrl", chars(500));

        doPut("/api/mentors/profile", register("MENTOR"), profile).andExpect(status().isCreated());
    }

    @Test
    void task_acceptsTheMaximumTitleDescriptionAndDuration() throws Exception {
        Mentor mentor = mentorWithProfile();

        doPost("/api/tasks", mentor.session(), Map.of(
                "title", chars(200), "description", chars(2000), "duration", 180, "category", "CAREER_ADVICE"))
                .andExpect(status().isCreated());
    }

    @Test
    void booking_acceptsTheMaximumStudentMessageAndMentorResponse() throws Exception {
        Session student = studentWithProfile();
        Mentor mentor = mentorWithProfile();
        long taskId = createTask(mentor.session());

        long bookingId = body(doPost("/api/bookings", student, Map.of(
                "mentorId", mentor.mentorId(), "taskId", taskId,
                "proposedDate", uniqueFutureSlot(), "studentMsg", chars(1000)))
                .andExpect(status().isCreated())).get("bookingId").asLong();

        doPut("/api/bookings/" + bookingId, mentor.session(), Map.of("status", "ACCEPTED", "mentorResp", chars(1000)))
                .andExpect(status().isOk());
    }
}
