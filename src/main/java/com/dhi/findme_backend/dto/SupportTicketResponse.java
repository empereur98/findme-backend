package com.dhi.findme_backend.dto;

import java.time.LocalDateTime;

public record SupportTicketResponse(
    String id,
    String code,
    String userName,
    String userEmail,
    String subject,
    String message,
    String type,
    String status,
    LocalDateTime date,
    String avatarUrl,
    String attachmentUrl
) {}
