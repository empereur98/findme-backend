package com.dhi.findme_backend.controller;

import com.dhi.findme_backend.dto.StandardResponse;
import com.dhi.findme_backend.dto.SupportAttachmentUploadResponse;
import com.dhi.findme_backend.dto.SupportTicketCreateRequest;
import com.dhi.findme_backend.dto.SupportTicketResponse;
import com.dhi.findme_backend.dto.SupportTicketTriageRequest;
import com.dhi.findme_backend.security.SecurityUtilsInterface;
import com.dhi.findme_backend.service.SupportTicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/support/tickets")
@Tag(name = "Support", description = "Gestion des tickets de support")
public class SupportController {

    private final SupportTicketService supportTicketService;
    private final SecurityUtilsInterface securityUtils;

    public SupportController(SupportTicketService supportTicketService, SecurityUtilsInterface securityUtils) {
        this.supportTicketService = supportTicketService;
        this.securityUtils = securityUtils;
    }

    @GetMapping
    @Operation(summary = "Liste des tickets", description = "Retourne les tickets de support de l'utilisateur (ou tous pour admin)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    })
    public ResponseEntity<StandardResponse<Page<SupportTicketResponse>>> getTickets(
            @Parameter(description = "Pagination") Pageable pageable,
            @Parameter(description = "Recherche") @RequestParam(required = false) String search,
            @Parameter(description = "Statut") @RequestParam(required = false) String status,
            @Parameter(description = "Type") @RequestParam(required = false) String type) {
        Page<SupportTicketResponse> response = supportTicketService.getTickets(pageable, search, status, type);
        return ResponseEntity.ok(StandardResponse.success(200, "Liste des tickets récupérée avec succès", response, "/api/support/tickets"));
    }

    @PostMapping
    @Operation(summary = "Créer ticket", description = "Crée un nouveau ticket de support")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Ticket créé avec succès")
    })
    public ResponseEntity<StandardResponse<SupportTicketResponse>> createTicket(@Valid @RequestBody SupportTicketCreateRequest request) {
        UUID currentUserId = securityUtils.getCurrentUserId();
        SupportTicketResponse response = supportTicketService.createTicket(request, currentUserId);
        return ResponseEntity.status(201).body(StandardResponse.success(201, "Ticket créé avec succès", response, "/api/support/tickets"));
    }

    @PostMapping("/upload-attachment")
    @Operation(summary = "Uploader pièce jointe", description = "Upload un fichier pour joindre à un ticket de support")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Fichier uploadé avec succès"),
        @ApiResponse(responseCode = "400", description = "Fichier invalide ou trop volumineux")
    })
    public ResponseEntity<StandardResponse<SupportAttachmentUploadResponse>> uploadAttachment(@RequestParam("file") MultipartFile file) {
        SupportAttachmentUploadResponse response = supportTicketService.uploadSupportAttachment(file);
        return ResponseEntity.ok(StandardResponse.success(200, "Fichier uploadé avec succès", response, "/api/support/tickets/upload-attachment"));
    }

    @PutMapping("/{id}/triage")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Trier un ticket (Admin)", description = "Change le statut ou d'autres propriétés d'un ticket")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ticket mis à jour avec succès"),
        @ApiResponse(responseCode = "403", description = "Accès refusé"),
        @ApiResponse(responseCode = "404", description = "Ticket non trouvé")
    })
    public ResponseEntity<StandardResponse<SupportTicketResponse>> triageTicket(
            @PathVariable UUID id,
            @Valid @RequestBody SupportTicketTriageRequest request) {
        SupportTicketResponse response = supportTicketService.triageTicket(id, request);
        return ResponseEntity.ok(StandardResponse.success(200, "Ticket mis à jour avec succès", response, "/api/support/tickets/" + id + "/triage"));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mettre à jour un ticket (Admin)", description = "Met à jour le statut d'un ticket de support")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ticket mis à jour avec succès"),
        @ApiResponse(responseCode = "403", description = "Accès refusé"),
        @ApiResponse(responseCode = "404", description = "Ticket non trouvé")
    })
    public ResponseEntity<StandardResponse<SupportTicketResponse>> updateTicketStatus(
            @PathVariable UUID id,
            @Valid @RequestBody SupportTicketTriageRequest request) {
        SupportTicketResponse response = supportTicketService.triageTicket(id, request);
        return ResponseEntity.ok(StandardResponse.success(200, "Statut du ticket mis à jour avec succès", response, "/api/support/tickets/" + id));
    }
}