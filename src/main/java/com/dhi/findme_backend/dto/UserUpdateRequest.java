package com.dhi.findme_backend.dto;

import com.dhi.findme_backend.validation.ValidPhone;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
    @Size(min = 2, message = "Le prénom doit contenir au moins 2 caractères")
    String firstName,

    @Size(min = 2, message = "Le nom doit contenir au moins 2 caractères")
    String lastName,

    @ValidPhone
    String phone,

    String defaultLocation,

    String avatarUrl
) {}
