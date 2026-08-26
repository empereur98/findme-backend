package com.dhi.findme_backend.controller;

import com.dhi.findme_backend.dto.PaymentResponse;
import com.dhi.findme_backend.dto.SubscriptionResponse;
import com.dhi.findme_backend.dto.SubscriptionUpgradeRequest;
import com.dhi.findme_backend.dto.StandardResponse;
import com.dhi.findme_backend.security.SecurityUtilsInterface;
import com.dhi.findme_backend.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/subscriptions")
@Tag(name = "Subscriptions", description = "Gestion des abonnements")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final SecurityUtilsInterface securityUtils;

    public SubscriptionController(SubscriptionService subscriptionService, SecurityUtilsInterface securityUtils) {
        this.subscriptionService = subscriptionService;
        this.securityUtils = securityUtils;
    }

    @PostMapping("/upgrade")
    @Operation(summary = "Passer en Pro", description = "Lance un paiement pour upgrader le plan free → pro")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Paiement initié avec succès")
    })
    public ResponseEntity<StandardResponse<PaymentResponse>> upgradeSubscription(@Valid @RequestBody SubscriptionUpgradeRequest request) {
        UUID currentUserId = securityUtils.getCurrentUserId();
        PaymentResponse response = subscriptionService.upgradeSubscription(request, currentUserId);
        return ResponseEntity.ok(StandardResponse.success(200, "Paiement initié avec succès", response, "/api/subscriptions/upgrade"));
    }

    @GetMapping("/me")
    @Operation(summary = "Statut abonnement", description = "Retourne le statut d'abonnement de l'utilisateur connecté")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Statut récupéré avec succès")
    })
    public ResponseEntity<StandardResponse<SubscriptionResponse>> getSubscriptionStatus() {
        UUID currentUserId = securityUtils.getCurrentUserId();
        SubscriptionResponse response = subscriptionService.getSubscriptionStatus(currentUserId);
        return ResponseEntity.ok(StandardResponse.success(200, "Statut d'abonnement récupéré avec succès", response, "/api/subscriptions/me"));
    }

    @DeleteMapping("/me")
    @Operation(summary = "Annuler abonnement", description = "Annule l'abonnement Pro (passage en free à la fin de la période)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Abonnement annulé avec succès")
    })
    public ResponseEntity<StandardResponse<Void>> cancelSubscription() {
        UUID currentUserId = securityUtils.getCurrentUserId();
        subscriptionService.cancelSubscription(currentUserId);
        return ResponseEntity.ok(StandardResponse.success(200, "Abonnement annulé avec succès", "/api/subscriptions/me"));
    }
}