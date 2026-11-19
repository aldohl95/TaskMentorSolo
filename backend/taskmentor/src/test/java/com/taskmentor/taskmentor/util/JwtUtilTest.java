package com.taskmentor.taskmentor.util;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilTest {

    private static final String SECRET = "test-secret-that-is-definitely-longer-than-32-bytes";
    private static final String OTHER_SECRET = "a-completely-different-secret-also-over-32-bytes!!";

    private static JwtUtil jwt(String secret, long expirationMs) {
        JwtUtil util = new JwtUtil(secret, expirationMs);
        util.init();
        return util;
    }

    @Test
    void roundTripsEmailAndRole() {
        JwtUtil util = jwt(SECRET, 60_000);
        String token = util.generateToken("student@college.edu", "STUDENT");

        assertEquals("student@college.edu", util.extractEmail(token));
        assertEquals("STUDENT", util.extractRole(token));
        assertTrue(util.validateToken(token, "student@college.edu"));
        assertFalse(util.validateToken(token, "someone-else@college.edu"));
    }

    @Test
    void rejectsTokenSignedWithAnotherKey() {
        String forged = jwt(OTHER_SECRET, 60_000).generateToken("mentor@example.com", "MENTOR");

        assertThrows(JwtException.class, () -> jwt(SECRET, 60_000).extractEmail(forged));
    }

    @Test
    void rejectsTamperedToken() {
        JwtUtil util = jwt(SECRET, 60_000);
        String token = util.generateToken("student@college.edu", "STUDENT");
        String tampered = token.substring(0, token.length() - 2) + (token.endsWith("A") ? "BB" : "AA");

        assertThrows(JwtException.class, () -> util.extractEmail(tampered));
    }

    @Test
    void rejectsExpiredToken() {
        JwtUtil util = jwt(SECRET, -1_000);
        String expired = util.generateToken("student@college.edu", "STUDENT");

        assertThrows(JwtException.class, () -> util.extractEmail(expired));
    }

    @Test
    void refusesToStartWithMissingWeakOrLeakedSecret() {
        assertThrows(IllegalStateException.class, () -> jwt("", 60_000));
        assertThrows(IllegalStateException.class, () -> jwt("too-short", 60_000));
        assertThrows(IllegalStateException.class,
                () -> jwt("your-secret-key-here-make-it-long-and-secure-at-least-256-bits", 60_000));
    }
}
