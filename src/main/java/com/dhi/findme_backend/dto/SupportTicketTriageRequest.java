package com.dhi.findme_backend.dto;

import jakarta.validation.constraints.NotBlank;

public record SupportTicketTriageRequest(
    @NotBlank(message = "Le statut est obligatoire")
    String status
    // Vous pouvez ajouter d'autres champs ici à l'avenir, comme la priorité
    // String priority;
) {}