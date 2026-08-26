package com.dhi.findme_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Customer(
        @JsonProperty("id")
        Long id,
        
        @JsonProperty("email")
        String email,
        
        @JsonProperty("name")
        String name,
        
        @JsonProperty("phone_number")
        String phoneNumber
) {
}
