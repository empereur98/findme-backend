package com.dhi.findme_backend.dto;

public record SettingsResponse(
    Boolean strictFormatting,
    Boolean autoCorrect,
    Integer validationThreshold,
    Integer rateLimit,
    Integer webhookTimeout
) {}
