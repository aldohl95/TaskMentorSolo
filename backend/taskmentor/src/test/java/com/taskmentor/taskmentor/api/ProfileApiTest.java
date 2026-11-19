package com.taskmentor.taskmentor.api;

import com.taskmentor.taskmentor.IntegrationTest;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProfileApiTest extends IntegrationTest {

    @Test
    void saveStudentProfile_returns201FirstTimeThen200AndUpdatesInPlace() throws Exception {
        Session student = register("STUDENT");

        doPut("/api/students/profile", student, Map.of("firstName", "Sam", "lastName", "Student", "major", "CS"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Sam"));

        doPut("/api/students/profile", student, Map.of("firstName", "Samuel", "lastName", "Student"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Samuel"));
    }

    @Test
    void getOwnProfile_beforeSavingOne_returns404() throws Exception {
        doGet("/api/students/profile", register("STUDENT")).andExpect(status().isNotFound());
        doGet("/api/mentors/profile", register("MENTOR")).andExpect(status().isNotFound());
    }

    @Test
    void listBookings_beforeSavingAProfile_returnsAnEmptyList() throws Exception {
        doGet("/api/bookings/student", register("STUDENT")).andExpect(status().isOk()).andExpect(jsonPath("$").isEmpty());
        doGet("/api/bookings/mentor", register("MENTOR")).andExpect(status().isOk()).andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void saveProfile_withMissingRequiredFields_returns400WithFieldErrors() throws Exception {
        doPut("/api/students/profile", register("STUDENT"), Map.of("bio", "no names"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.firstName").value("First name is required"))
                .andExpect(jsonPath("$.errors.lastName").value("Last name is required"));
    }

    @Test
    void saveProfile_withOverlongText_returns400() throws Exception {
        doPut("/api/students/profile", register("STUDENT"),
                Map.of("firstName", "Sam", "lastName", "Student", "bio", "x".repeat(2001)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.bio").exists());
    }

    // ---- role enforcement: the role is no longer cosmetic -----------------------------------

    @Test
    void aStudentCannotBecomeAMentor() throws Exception {
        Map<String, String> mentorProfile = Map.of(
                "firstName", "Eve", "lastName", "Student", "roleTitle", "Boss", "company", "Acme");
        doPut("/api/mentors/profile", register("STUDENT"), mentorProfile).andExpect(status().isForbidden());
    }

    @Test
    void aMentorCannotSaveAStudentProfile() throws Exception {
        doPut("/api/students/profile", register("MENTOR"), Map.of("firstName", "Mia", "lastName", "Mentor"))
                .andExpect(status().isForbidden());
    }

    // ---- visibility ---------------------------------------------------------------------------

    @Test
    void mentorProfiles_arePublic() throws Exception {
        Mentor mentor = mentorWithProfile();

        doGet("/api/mentors/" + mentor.mentorId() + "/profile", null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Mia"));
        doGet("/api/mentors/search", null).andExpect(status().isOk());
    }

    @Test
    void unknownMentor_returns404() throws Exception {
        doGet("/api/mentors/999999/profile", null).andExpect(status().isNotFound());
    }

    @Test
    void studentProfiles_arePrivate_visibleOnlyToTheOwnerAndBookedMentors() throws Exception {
        Session owner = studentWithProfile();
        long studentId = body(doGet("/api/students/profile", owner)).get("studentId").asLong();
        String url = "/api/students/" + studentId + "/profile";

        Mentor bookedMentor = mentorWithProfile();
        long taskId = createTask(bookedMentor.session());
        createBooking(owner, bookedMentor.mentorId(), taskId);

        doGet(url, owner).andExpect(status().isOk());
        doGet(url, bookedMentor.session()).andExpect(status().isOk());

        // Anyone else gets a 404, so the profile's existence is not revealed.
        doGet(url, studentWithProfile()).andExpect(status().isNotFound());
        doGet(url, mentorWithProfile().session()).andExpect(status().isNotFound());
        doGet(url, null).andExpect(status().isUnauthorized());
    }
}
