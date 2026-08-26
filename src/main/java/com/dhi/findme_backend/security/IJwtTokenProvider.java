package com.dhi.findme_backend.security;

import org.springframework.security.core.Authentication;

public interface IJwtTokenProvider {
    String generateToken(Authentication authentication);
    String getUsernameFromJWT(String token);
    boolean validateToken(String authToken);
}