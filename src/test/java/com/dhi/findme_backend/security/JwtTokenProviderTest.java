package com.dhi.findme_backend.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private static final String SECRET = "findme-secret-key-for-jwt-token-generation-2024-test-value";
    private static final long EXPIRATION_MS = 3600000L;

    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(tokenProvider, "jwtSecret", SECRET);
        ReflectionTestUtils.setField(tokenProvider, "jwtExpirationInMs", EXPIRATION_MS);
    }

    private Authentication authenticationWithUserDetails(String email) {
        UserDetails userDetails = User.builder()
                .username(email)
                .password("encoded")
                .authorities(Collections.emptyList())
                .build();
        return new UsernamePasswordAuthenticationToken(userDetails, null, Collections.emptyList());
    }

    @Test
    void generateToken_thenUsernameCanBeReadBack() {
        String token = tokenProvider.generateToken(authenticationWithUserDetails("user@example.com"));

        assertNotNull(token);
        assertEquals("user@example.com", tokenProvider.getUsernameFromJWT(token));
        assertTrue(tokenProvider.validateToken(token));
    }

    @Test
    void generateToken_whenPrincipalIsNotUserDetails_usesPrincipalToString() {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("plain@example.com", null, Collections.emptyList());

        String token = tokenProvider.generateToken(authentication);

        assertEquals("plain@example.com", tokenProvider.getUsernameFromJWT(token));
    }

    @Test
    void validateToken_whenSignedWithAnotherSecret_returnsFalse() {
        String foreignToken = Jwts.builder()
                .subject("user@example.com")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(Keys.hmacShaKeyFor("another-secret-key-that-is-long-enough-for-hs256".getBytes()))
                .compact();

        assertFalse(tokenProvider.validateToken(foreignToken));
    }

    @Test
    void validateToken_whenExpired_returnsFalse() {
        ReflectionTestUtils.setField(tokenProvider, "jwtExpirationInMs", -1000L);

        String expiredToken = tokenProvider.generateToken(authenticationWithUserDetails("user@example.com"));

        assertFalse(tokenProvider.validateToken(expiredToken));
    }

    @Test
    void validateToken_whenMalformedOrEmpty_returnsFalse() {
        assertFalse(tokenProvider.validateToken("not-a-jwt"));
        assertFalse(tokenProvider.validateToken(""));
    }

    @Test
    void generateToken_setsExpirationFromConfiguredDuration() {
        String token = tokenProvider.generateToken(authenticationWithUserDetails("user@example.com"));

        Date expiration = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(SECRET.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();

        long remaining = expiration.getTime() - System.currentTimeMillis();
        assertTrue(remaining > 0 && remaining <= EXPIRATION_MS, "remaining=" + remaining);
    }
}
