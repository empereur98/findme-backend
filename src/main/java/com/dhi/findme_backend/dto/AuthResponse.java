package com.dhi.findme_backend.dto;

import java.time.LocalDate;

public record AuthResponse(
    String token,
    UserResponse user
) {}
