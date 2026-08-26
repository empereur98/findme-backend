package com.dhi.findme_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CampayWebhookEvent(
        @JsonProperty("reference")
        String reference,

        @JsonProperty("external_reference")
        String externalReference,

        @JsonProperty("status")
        String status,

        @JsonProperty("amount")
        String amount,

        @JsonProperty("currency")
        String currency,

        @JsonProperty("operator")
        String operator,

        @JsonProperty("code")
        String code,

        @JsonProperty("operator_reference")
        String operatorReference,

        @JsonProperty("phone_number")
        String phoneNumber,

        @JsonProperty("customer_email")
        String customerEmail
) {
}
