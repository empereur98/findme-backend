package com.dhi.findme_backend.controller;

import com.dhi.findme_backend.dto.ExportResponse;
import com.dhi.findme_backend.dto.StandardResponse;
import com.dhi.findme_backend.security.SecurityUtilsInterface;
import com.dhi.findme_backend.service.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/exports")
@Tag(name = "Exports", description = "Gestion des exports PDF")
public class ExportController {

    private final ExportService exportService;
    private final SecurityUtilsInterface securityUtils;

    public ExportController(ExportService exportService, SecurityUtilsInterface securityUtils) {
        this.exportService = exportService;
        this.securityUtils = securityUtils;
    }

    @PostMapping("/pdf/{addressId}")
    @PreAuthorize("hasRole('PRO')")
    @Operation(summary = "Générer PDF", description = "Génère un certificat PDF officiel pour une adresse (plan Pro uniquement)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "PDF généré avec succès"),
        @ApiResponse(responseCode = "403", description = "Plan Pro requis")
    })
    public ResponseEntity<StandardResponse<ExportResponse>> generatePdfExport(@PathVariable UUID addressId) {
        UUID currentUserId = securityUtils.getCurrentUserId();
        ExportResponse response = exportService.generatePdfExport(addressId, currentUserId);
        return ResponseEntity.ok(StandardResponse.success(200, "PDF généré avec succès", response, "/api/exports/pdf/" + addressId));
    }

    @GetMapping("/history")
    @Operation(summary = "Historique exports", description = "Retourne l'historique des exports PDF de l'utilisateur")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Historique récupéré avec succès")
    })
    public ResponseEntity<StandardResponse<Page<ExportResponse>>> getExportHistory(
            @Parameter(description = "Pagination") Pageable pageable) {
        UUID currentUserId = securityUtils.getCurrentUserId();
        Page<ExportResponse> response = exportService.getExportHistory(pageable, currentUserId);
        return ResponseEntity.ok(StandardResponse.success(200, "Historique des exports récupéré avec succès", response, "/api/exports/history"));
    }
}
