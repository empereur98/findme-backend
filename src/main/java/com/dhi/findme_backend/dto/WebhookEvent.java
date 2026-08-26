package com.dhi.findme_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record WebhookEvent(
        @JsonProperty("event")
        String event,
        
        @JsonProperty("data")
        WebhookData data
) {
}
