package com.taskmentor.taskmentor.auth;

import com.taskmentor.taskmentor.IntegrationTest;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthApiTest extends IntegrationTest {

    private static final String TEST_SECRET = "test-only-signing-secret-long-enough-for-hs256-0123456789abcdef";

    private static String uniqueEmail() {
        return "auth." + UUID.randomUUID() + "@example.com";
    }

    @Test
    void register_returns201WithTokenAndRole() throws Exception {
        doPost("/api/auth/register", null, Map.of("email", uniqueEmail(), "password", "password123", "role", "student"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token", not(emptyString())))
                .andExpect(jsonPath("$.role").value("STUDENT"))
                .andExpect(jsonPath("$.userId").isNumber());
    }

    @Test
    void register_duplicateEmail_returns409ProblemDetail() throws Exception {
        String email = uniqueEmail();
        Map<String, String> request = Map.of("email", email, "password", "password123", "role", "STUDENT");
        doPost("/api/auth/register", null, request).andExpect(status().isCreated());

        doPost("/api/auth/register", null, request)
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.detail").value("Email already registered"));
    }

    @Test
    void register_invalidBody_returns400WithFieldErrors() throws Exception {
        doPost("/api/auth/register", null, Map.of("email", "nope", "password", "123", "role", "STUDENT"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Validation failed"))
                .andExpect(jsonPath("$.errors.email").value("Invalid email format"))
                .andExpect(jsonPath("$.errors.password").exists());
    }

    @Test
    void register_cannotChooseAnUnknownRole() throws Exception {
        doPost("/api/auth/register", null, Map.of("email", uniqueEmail(), "password", "password123", "role", "ADMIN"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.role").exists());
    }

    @Test
    void register_malformedJson_returns400() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType("application/json").content("{not json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_withCorrectPassword_returns200WithToken() throws Exception {
        String email = uniqueEmail();
        doPost("/api/auth/register", null, Map.of("email", email, "password", "password123", "role", "MENTOR"))
                .andExpect(status().isCreated());

        doPost("/api/auth/login", null, Map.of("email", email, "password", "password123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", not(emptyString())))
                .andExpect(jsonPath("$.role").value("MENTOR"));
    }

    @Test
    void login_wrongPasswordAndUnknownEmail_bothReturn401WithTheSameMessage() throws Exception {
        String email = uniqueEmail();
        doPost("/api/auth/register", null, Map.of("email", email, "password", "password123", "role", "STUDENT"))
                .andExpect(status().isCreated());

        doPost("/api/auth/login", null, Map.of("email", email, "password", "wrong-password"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("Invalid email or password"));

        doPost("/api/auth/login", null, Map.of("email", uniqueEmail(), "password", "password123"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("Invalid email or password"));
    }

    @Test
    void protectedEndpoint_withoutToken_returns401() throws Exception {
        doGet("/api/tasks", null).andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoint_withGarbageToken_returns401() throws Exception {
        doGet("/api/tasks", new Session("not.a.jwt", 0, "x")).andExpect(status().isUnauthorized());
    }

    /** Regression: this placeholder secret was committed to a public repo, so anyone could forge tokens. */
    @Test
    void protectedEndpoint_withTokenSignedByTheOldLeakedSecret_returns401() throws Exception {
        Session real = register("STUDENT");
        String forged = Jwts.builder()
                .subject(real.email())
                .claim("role", "STUDENT")
                .expiration(new Date(System.currentTimeMillis() + 3_600_000))
                .signWith(Keys.hmacShaKeyFor(
                        "your-secret-key-here-make-it-long-and-secure-at-least-256-bits".getBytes(StandardCharsets.UTF_8)))
                .compact();

        doGet("/api/tasks", new Session(forged, real.userId(), real.email())).andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoint_withTamperedToken_returns401() throws Exception {
        Session real = register("STUDENT");
        String tampered = real.token().substring(0, real.token().length() - 3) + "abc";

        doGet("/api/tasks", new Session(tampered, real.userId(), real.email())).andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoint_withExpiredToken_returns401() throws Exception {
        Session real = register("STUDENT");
        // Correctly signed with the test secret (application-test.properties), but already expired.
        String expired = Jwts.builder()
                .subject(real.email())
                .claim("role", "STUDENT")
                .expiration(new Date(System.currentTimeMillis() - 60_000))
                .signWith(Keys.hmacShaKeyFor(TEST_SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();

        doGet("/api/tasks", new Session(expired, real.userId(), real.email())).andExpect(status().isUnauthorized());
    }

    @Test
    void errorResponses_doNotLeakInternals() throws Exception {
        doGet("/api/tasks/999999", register("STUDENT"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(not(containsString("Exception"))))
                .andExpect(content().string(not(containsString("SELECT"))));
    }
}
