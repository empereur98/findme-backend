package com.dhi.findme_backend.dto;

import java.math.BigDecimal;

public record PaymentResponse(
        Boolean success,
        String message,
        String paymentLink,
        String transactionId,
        String status,
        BigDecimal amount,
        String currency,
        String txRef
) {
}
