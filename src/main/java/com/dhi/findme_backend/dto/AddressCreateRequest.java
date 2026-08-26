package com.dhi.findme_backend.dto;

import com.dhi.findme_backend.validation.ValidCountryCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AddressCreateRequest(
    @NotBlank(message = "Le nom est requis")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    String name,

    @NotBlank(message = "Le pays est requis")
    @ValidCountryCode
    String country,

    @NotBlank(message = "La ville est requise")
    @Size(max = 100, message = "La ville ne peut pas dépasser 100 caractères")
    String city,

    @NotBlank(message = "Le quartier est requis")
    @Size(max = 100, message = "Le quartier ne peut pas dépasser 100 caractères")
    String district,

    @Size(max = 255, message = "La rue ne peut pas dépasser 255 caractères")
    String street,

    @NotBlank(message = "Le repère est requis")
    @Size(max = 500, message = "Le repère ne peut pas dépasser 500 caractères")
    String landmark,

    @NotNull(message = "La latitude GPS est requise")
    Double gpsLat,

    @NotNull(message = "La longitude GPS est requise")
    Double gpsLng,

    String imageFacade,

    @NotBlank(message = "Le type est requis")
    String type
) {}
