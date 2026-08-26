package com.dhi.findme_backend.dto;

import jakarta.validation.constraints.NotBlank;

public record UserPlanUpdateRequest(
    @NotBlank(message = "Le plan est obligatoire")
    String plan
) {}