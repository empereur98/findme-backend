package com.dhi.findme_backend.controller;

import com.dhi.findme_backend.dto.CampayWebhookEvent;
import com.dhi.findme_backend.dto.PaymentRequest;
import com.dhi.findme_backend.dto.PaymentResponse;
import com.dhi.findme_backend.dto.StandardResponse;
import com.dhi.findme_backend.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payments", description = "Gestion des paiements via Flutterwave")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/initiate")
    @Operation(summary = "Initier un paiement", description = "Crée une transaction de paiement via Flutterwave")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Paiement initié avec succès"),
        @ApiResponse(responseCode = "400", description = "Erreur lors de l'initiation")
    })
    public ResponseEntity<StandardResponse<PaymentResponse>> initiatePayment(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.initiatePayment(request);
        return ResponseEntity.ok(StandardResponse.success(200, "Paiement initié avec succès", response, "/api/payments/initiate"));
    }

    @GetMapping("/verify/{transactionId}")
    @Operation(summary = "Vérifier une transaction", description = "Vérifie le statut d'une transaction Flutterwave")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transaction vérifiée"),
        @ApiResponse(responseCode = "404", description = "Transaction introuvable")
    })
    public ResponseEntity<StandardResponse<PaymentResponse>> verifyTransaction(@PathVariable String transactionId) {
        PaymentResponse response = paymentService.verifyTransaction(transactionId);
        return ResponseEntity.ok(StandardResponse.success(200, "Transaction vérifiée", response, "/api/payments/verify/" + transactionId));
    }

    @PostMapping("/webhook/campay")
    @Operation(summary = "Webhook Campay", description = "Reçoit les événements de paiement de Campay")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Webhook traité avec succès")
    })
    public ResponseEntity<StandardResponse<Void>> handleCampayWebhook(@RequestBody CampayWebhookEvent webhookEvent) {
        paymentService.handleWebhook(webhookEvent);
        return ResponseEntity.ok(StandardResponse.success(200, "Webhook traité avec succès", "/api/payments/webhook/campay"));
    }

    @PostMapping("/test/webhook/campay")
    @Operation(summary = "Test Webhook Campay", description = "Simule un webhook Campay réussi pour les tests (uniquement en démo)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Webhook simulé avec succès")
    })
    public ResponseEntity<StandardResponse<Void>> testCampayWebhook(@RequestParam String email) {
        // Créer un événement webhook simulé pour tester
        CampayWebhookEvent testEvent = new CampayWebhookEvent(
            "test-ref-" + System.currentTimeMillis(),
            "test-external-ref",
            "SUCCESSFUL",
            "10",
            "XOF",
            "Orange",
            "TEST123",
            "OP456",
            "237697560705",
            email
        );
        paymentService.handleWebhook(testEvent);
        return ResponseEntity.ok(StandardResponse.success(200, "Webhook simulé avec succès", "/api/payments/test/webhook/campay"));
    }
}
