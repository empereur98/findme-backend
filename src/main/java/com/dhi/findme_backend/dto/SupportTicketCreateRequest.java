package com.dhi.findme_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SupportTicketCreateRequest(
    @NotBlank(message = "Le sujet est requis")
    @Size(max = 200, message = "Le sujet ne peut pas dépasser 200 caractères")
    String subject,

    @NotBlank(message = "Le message est requis")
    @Size(max = 2000, message = "Le message ne peut pas dépasser 2000 caractères")
    String message,

    @NotBlank(message = "Le type est requis")
    String type,

    @Size(max = 500, message = "L'URL de la pièce jointe ne peut pas dépasser 500 caractères")
    String attachmentUrl
) {}
