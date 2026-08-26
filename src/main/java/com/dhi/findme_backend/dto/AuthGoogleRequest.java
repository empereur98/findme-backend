package com.dhi.findme_backend.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthGoogleRequest(
    @NotBlank(message = "Le token Google est requis")
    String googleAccessToken
) {}
