package com.dhi.findme_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record WebhookData(
        @JsonProperty("id")
        Long id,
        
        @JsonProperty("tx_ref")
        String txRef,
        
        @JsonProperty("flw_ref")
        String flwRef,
        
        @JsonProperty("amount")
        BigDecimal amount,
        
        @JsonProperty("currency")
        String currency,
        
        @JsonProperty("customer")
        Customer customer,
        
        @JsonProperty("status")
        String status,
        
        @JsonProperty("payment_type")
        String paymentType,
        
        @JsonProperty("created_at")
        String createdAt
) {
}
