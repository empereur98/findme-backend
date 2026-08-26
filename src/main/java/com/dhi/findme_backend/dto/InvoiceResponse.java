package com.dhi.findme_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record InvoiceResponse(
    UUID id,
    String invoiceNumber,
    BigDecimal amount,
    LocalDate issueDate,
    LocalDate dueDate,
    String status,
    String description
) {}