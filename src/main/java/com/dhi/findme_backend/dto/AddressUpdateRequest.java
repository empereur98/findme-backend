package com.dhi.findme_backend.dto;

import jakarta.validation.constraints.Size;

public record AddressUpdateRequest(
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    String name,

    @Size(max = 100, message = "Le quartier ne peut pas dépasser 100 caractères")
    String district,

    @Size(max = 255, message = "La rue ne peut pas dépasser 255 caractères")
    String street,

    @Size(max = 500, message = "Le repère ne peut pas dépasser 500 caractères")
    String landmark,

    Double gpsLat,

    Double gpsLng,

    String imageFacade,

    String type
) {}
