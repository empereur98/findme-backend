package com.dhi.findme_backend.dto;

import java.time.LocalDate;

public record UserResponse(
    String id,
    String firstName,
    String lastName,
    String email,
    String phone,
    String avatarUrl,
    String defaultLocation,
    Boolean verified,
    String role,
    Integer addressesCreatedCount,
    Integer maxAddresses,
    String plan,
    LocalDate registrationDate
) {}
