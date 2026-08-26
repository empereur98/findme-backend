package com.dhi.findme_backend.dto;

public record Features(
    Boolean pdfExport,
    Boolean qrCode,
    Boolean verifiedBadge,
    Boolean apiAccess
) {}
