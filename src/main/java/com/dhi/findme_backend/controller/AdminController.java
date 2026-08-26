package com.dhi.findme_backend.controller;

import com.dhi.findme_backend.dto.AdminStatsResponse;
import com.dhi.findme_backend.dto.StandardResponse;
import com.dhi.findme_backend.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Endpoints d'administration")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Statistiques globales", description = "Récupère les statistiques clés de l'application (Admin uniquement)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Statistiques récupérées avec succès"),
        @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<StandardResponse<AdminStatsResponse>> getStats() {
        AdminStatsResponse response = adminService.getStats();
        return ResponseEntity.ok(StandardResponse.success(200, "Statistiques récupérées avec succès", response, "/api/admin/stats"));
    }
}