package com.taskmentor.taskmentor.api;

import com.taskmentor.taskmentor.IntegrationTest;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.Matchers.endsWith;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TaskApiTest extends IntegrationTest {

    private static Map<String, Object> taskBody(int duration) {
        return Map.of("title", "Mock Interview", "description", "Practice coding interviews",
                "duration", duration, "category", "MOCK_INTERVIEW");
    }

    @Test
    void createTask_returns201WithLocationHeader() throws Exception {
        Mentor mentor = mentorWithProfile();

        var result = doPost("/api/tasks", mentor.session(), taskBody(60))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/api/tasks/")))
                .andExpect(jsonPath("$.mentorId").value(mentor.mentorId()));

        long taskId = body(result).get("taskId").asLong();
        result.andExpect(header().string("Location", endsWith("/api/tasks/" + taskId)));
        doGet("/api/tasks/" + taskId, mentor.session()).andExpect(status().isOk());
    }

    @Test
    void createTask_beforeSavingAMentorProfile_returns409WithAHelpfulMessage() throws Exception {
        doPost("/api/tasks", register("MENTOR"), taskBody(30))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Complete your mentor profile before creating tasks"));
    }

    @Test
    void createTask_asAStudent_returns403() throws Exception {
        doPost("/api/tasks", register("STUDENT"), taskBody(30)).andExpect(status().isForbidden());
    }

    @Test
    void createTask_withInvalidDuration_returns400() throws Exception {
        Mentor mentor = mentorWithProfile();
        doPost("/api/tasks", mentor.session(), taskBody(5))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.duration").value("Duration must be at least 15 minutes"));
        doPost("/api/tasks", mentor.session(), taskBody(500))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.duration").value("Duration must be at most 180 minutes"));
    }

    @Test
    void createTask_acceptsEveryCategory_includingCareerAdvice() throws Exception {
        Mentor mentor = mentorWithProfile();
        for (String category : new String[]{"RESUME_REVIEW", "MOCK_INTERVIEW", "CAREER_ADVICE"}) {
            doPost("/api/tasks", mentor.session(), Map.of(
                    "title", "T", "description", "D", "duration", 30, "category", category))
                    .andExpect(status().isCreated());
        }
    }

    @Test
    void unknownTask_returns404() throws Exception {
        doGet("/api/tasks/999999", register("STUDENT")).andExpect(status().isNotFound());
    }

    @Test
    void invalidCategoryInPath_returns400() throws Exception {
        doGet("/api/tasks/category/NOT_A_CATEGORY", register("STUDENT")).andExpect(status().isBadRequest());
    }

    @Test
    void updateTask_byItsOwner_returns200() throws Exception {
        Mentor mentor = mentorWithProfile();
        long taskId = createTask(mentor.session());

        doPut("/api/tasks/" + taskId, mentor.session(), taskBody(45))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duration").value(45));
    }

    @Test
    void updateAndDeleteTask_byAnotherMentor_return403() throws Exception {
        long taskId = createTask(mentorWithProfile().session());
        Session otherMentor = mentorWithProfile().session();

        doPut("/api/tasks/" + taskId, otherMentor, taskBody(45)).andExpect(status().isForbidden());
        doDelete("/api/tasks/" + taskId, otherMentor).andExpect(status().isForbidden());
    }

    @Test
    void deleteTask_returns204ThenTheTaskIsGone() throws Exception {
        Mentor mentor = mentorWithProfile();
        long taskId = createTask(mentor.session());

        doDelete("/api/tasks/" + taskId, mentor.session()).andExpect(status().isNoContent());
        doGet("/api/tasks/" + taskId, mentor.session()).andExpect(status().isNotFound());
        doDelete("/api/tasks/" + taskId, mentor.session()).andExpect(status().isNotFound());
    }
}
