package com.dhi.findme_backend.controller;

import com.dhi.findme_backend.dto.InvoiceResponse;
import com.dhi.findme_backend.dto.StandardResponse;
import com.dhi.findme_backend.security.SecurityUtils;
import com.dhi.findme_backend.service.BillingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/billing")
@Tag(name = "Billing", description = "Gestion de la facturation")
public class BillingController {

    private final BillingService billingService;
    private final SecurityUtils securityUtils;

    public BillingController(BillingService billingService, SecurityUtils securityUtils) {
        this.billingService = billingService;
        this.securityUtils = securityUtils;
    }

    @GetMapping("/invoices")
    @Operation(summary = "Liste des factures", description = "Récupère l'historique des factures de l'utilisateur connecté")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Factures récupérées avec succès")
    })
    public ResponseEntity<StandardResponse<Page<InvoiceResponse>>> getInvoices(@Parameter(description = "Pagination") Pageable pageable) {
        UUID currentUserId = securityUtils.getCurrentUserId();
        Page<InvoiceResponse> response = billingService.getInvoicesForUser(currentUserId, pageable);
        return ResponseEntity.ok(StandardResponse.success(200, "Factures récupérées avec succès", response, "/api/billing/invoices"));
    }
}