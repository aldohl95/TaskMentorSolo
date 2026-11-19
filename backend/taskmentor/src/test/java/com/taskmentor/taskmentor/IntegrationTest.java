package com.taskmentor.taskmentor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Base class for tests that exercise the real HTTP API against a real PostgreSQL.
 * One container is shared by every test class (Spring caches the application context, so the
 * datasource URL must stay stable). Tests isolate themselves with unique emails instead of cleanup.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class IntegrationTest {

    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");
    private static final AtomicInteger SLOT_COUNTER = new AtomicInteger();

    static {
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void datasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    public record Session(String token, long userId, String email) {
    }

    // ---- request helpers -------------------------------------------------

    protected ResultActions doGet(String url, Session as) throws Exception {
        return mockMvc.perform(authorize(get(url), as));
    }

    protected ResultActions doPost(String url, Session as, Object body) throws Exception {
        return mockMvc.perform(authorize(post(url), as).contentType(MediaType.APPLICATION_JSON).content(json(body)));
    }

    protected ResultActions doPut(String url, Session as, Object body) throws Exception {
        return mockMvc.perform(authorize(put(url), as).contentType(MediaType.APPLICATION_JSON).content(json(body)));
    }

    protected ResultActions doDelete(String url, Session as) throws Exception {
        return mockMvc.perform(authorize(delete(url), as));
    }

    private MockHttpServletRequestBuilder authorize(MockHttpServletRequestBuilder request, Session as) {
        return as == null ? request : request.header("Authorization", "Bearer " + as.token());
    }

    protected String json(Object body) throws Exception {
        return objectMapper.writeValueAsString(body);
    }

    protected JsonNode body(ResultActions result) throws Exception {
        return objectMapper.readTree(result.andReturn().getResponse().getContentAsString());
    }

    // ---- fixtures --------------------------------------------------------

    protected Session register(String role) throws Exception {
        String email = role.toLowerCase() + "." + UUID.randomUUID() + "@example.com";
        JsonNode response = body(doPost("/api/auth/register", null,
                Map.of("email", email, "password", "password123", "role", role))
                .andExpect(status().isCreated()));
        return new Session(response.get("token").asText(), response.get("userId").asLong(), email);
    }

    /** Registers a student and saves their profile. Returns the session. */
    protected Session studentWithProfile() throws Exception {
        Session student = register("STUDENT");
        doPut("/api/students/profile", student, Map.of("firstName", "Sam", "lastName", "Student"))
                .andExpect(status().isCreated());
        return student;
    }

    public record Mentor(Session session, long mentorId) {
    }

    /** Registers a mentor and saves their profile. */
    protected Mentor mentorWithProfile() throws Exception {
        Session mentor = register("MENTOR");
        JsonNode profile = body(doPut("/api/mentors/profile", mentor, Map.of(
                "firstName", "Mia", "lastName", "Mentor", "roleTitle", "Engineer", "company", "Acme"))
                .andExpect(status().isCreated()));
        return new Mentor(mentor, profile.get("mentorId").asLong());
    }

    protected long createTask(Session mentor) throws Exception {
        return body(doPost("/api/tasks", mentor, Map.of(
                "title", "Resume Review", "description", "I will review your resume",
                "duration", 30, "category", "RESUME_REVIEW"))
                .andExpect(status().isCreated())).get("taskId").asLong();
    }

    /** A unique future slot, so tests never collide on the same mentor/time. */
    protected String uniqueFutureSlot() {
        return LocalDateTime.now().plusDays(7).withNano(0).withSecond(0)
                .plusMinutes(SLOT_COUNTER.incrementAndGet()).toString();
    }

    protected Map<String, Object> bookingRequest(long mentorId, long taskId, String proposedDate) {
        return Map.of("mentorId", mentorId, "taskId", taskId, "proposedDate", proposedDate, "studentMsg", "Hello!");
    }

    protected long createBooking(Session student, long mentorId, long taskId) throws Exception {
        return body(doPost("/api/bookings", student, bookingRequest(mentorId, taskId, uniqueFutureSlot()))
                .andExpect(status().isCreated())).get("bookingId").asLong();
    }

    @BeforeEach
    void ensureContainerRunning() {
        if (!POSTGRES.isRunning()) {
            throw new IllegalStateException("Test Postgres container is not running");
        }
    }
}
