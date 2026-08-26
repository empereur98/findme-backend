package com.dhi.findme_backend.controller;

import com.dhi.findme_backend.dto.SettingsRequest;
import com.dhi.findme_backend.dto.SettingsResponse;
import com.dhi.findme_backend.dto.StandardResponse;
import com.dhi.findme_backend.service.SettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@Tag(name = "Settings", description = "Gestion des paramètres système")
public class SettingsController {

    private final SettingsService settingsService;

    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lire configuration", description = "Récupère la configuration système actuelle (Admin uniquement)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Configuration récupérée avec succès"),
        @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<StandardResponse<SettingsResponse>> getSettings() {
        SettingsResponse response = settingsService.getSettings();
        return ResponseEntity.ok(StandardResponse.success(200, "Configuration récupérée avec succès", response, "/api/settings"));
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mettre à jour configuration", description = "Met à jour la configuration système (Admin uniquement)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Configuration mise à jour avec succès"),
        @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<StandardResponse<SettingsResponse>> updateSettings(@Valid @RequestBody SettingsRequest request) {
        SettingsResponse response = settingsService.updateSettings(request);
        return ResponseEntity.ok(StandardResponse.success(200, "Configuration mise à jour avec succès", response, "/api/settings"));
    }
}
