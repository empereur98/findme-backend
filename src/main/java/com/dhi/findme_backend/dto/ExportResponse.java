package com.dhi.findme_backend.dto;

import java.time.LocalDateTime;

public record ExportResponse(
    String id,
    String filename,
    String downloadUrl,
    LocalDateTime expiresAt,
    String size
) {}
