package com.dhi.findme_backend.dto;

public record SettingsRequest(
    Boolean strictFormatting,
    Boolean autoCorrect,
    Integer validationThreshold,
    Integer rateLimit,
    Integer webhookTimeout
) {}
