package com.fintrack.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JwtService — no Spring context needed, just plain JUnit.
 * Run with: mvn test
 */
class JwtServiceTest {

    // A 32+ char secret is required for HS256.
    private final JwtService jwtService =
            new JwtService("test-secret-key-that-is-long-enough-1234567890", 3_600_000L);

    @Test
    void generatesTokenAndReadsBackEmail() {
        String token = jwtService.generateToken("alice@example.com");

        assertNotNull(token);
        assertEquals("alice@example.com", jwtService.extractEmail(token));
    }

    @Test
    void validTokenIsAcceptedForSameUser() {
        String token = jwtService.generateToken("bob@example.com");

        assertTrue(jwtService.isTokenValid(token, "bob@example.com"));
        assertFalse(jwtService.isTokenValid(token, "someone-else@example.com"));
    }

    @Test
    void expiredTokenIsRejected() {
        // expiration = -1000ms means it expired 1 second in the past.
        JwtService shortLived =
                new JwtService("test-secret-key-that-is-long-enough-1234567890", -1000L);
        String token = shortLived.generateToken("carol@example.com");

        assertFalse(shortLived.isTokenValid(token, "carol@example.com"));
    }
}
