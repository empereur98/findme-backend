package com.dhi.findme_backend.service.impl;

import com.dhi.findme_backend.dto.CampayCollectRequest;
import com.dhi.findme_backend.dto.CampayCollectResponse;
import com.dhi.findme_backend.dto.CampayWebhookEvent;
import com.dhi.findme_backend.dto.NotificationCreateRequest;
import com.dhi.findme_backend.dto.PaymentRequest;
import com.dhi.findme_backend.dto.PaymentResponse;
import com.dhi.findme_backend.entity.User;
import com.dhi.findme_backend.repository.UserRepository;
import com.dhi.findme_backend.service.NotificationService;
import com.dhi.findme_backend.service.PaymentService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private static final int PREMIUM_MAX_ADDRESSES = 100;

    @Value("${campay.api-key}")
    private String campayApiKey;

    @Value("${campay.base-url}")
    private String campayBaseUrl;

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public PaymentServiceImpl(UserRepository userRepository, NotificationService notificationService, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.restTemplate = restTemplate;
    }

    @Override
    public PaymentResponse initiatePayment(PaymentRequest request) {
        // Vérifier la limitation de montant pour l'API démo Campay
        if (campayBaseUrl.contains("demo.campay.net")) {
            BigDecimal maxDemoAmount = new BigDecimal("25.00");
            if (request.amount().compareTo(maxDemoAmount) > 0) {
                return new PaymentResponse(false, 
                    "L'API démo Campay limite le montant à 25.00 XAF. Montant demandé: " + request.amount() + " " + request.currency(), 
                    null, null, "failed", null, null, null);
            }
        }
        
        String url = campayBaseUrl + "/collect/";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Token " + campayApiKey);
        
        // Nettoyer le numéro de téléphone pour retirer le signe + requis par Campay
        String cleanPhoneNumber = request.phoneNumber().replace("+", "");
        
        CampayCollectRequest campayRequest = new CampayCollectRequest(
            request.amount(),
            request.currency(),
            cleanPhoneNumber,
            "Abonnement FindMe Pro",
            request.txRef()
        );
        
        HttpEntity<CampayCollectRequest> entity = new HttpEntity<>(campayRequest, headers);
        
        try {
            ResponseEntity<CampayCollectResponse> response = restTemplate.exchange(
                url, 
                HttpMethod.POST, 
                entity, 
                CampayCollectResponse.class
            );
            
            LOGGER.info("Campay response status: {}, body: {}", response.getStatusCode(), response.getBody());
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                CampayCollectResponse campayResponse = response.getBody();
                return new PaymentResponse(
                    true,
                    "Paiement initié avec succès. " + (campayResponse.ussdCode() != null ? "USSD: " + campayResponse.ussdCode() : "En attente de confirmation"),
                    null,
                    campayResponse.reference(),
                    campayResponse.status() != null ? campayResponse.status() : "pending",
                    campayResponse.amount() != null ? new BigDecimal(campayResponse.amount()) : null,
                    campayResponse.currency(),
                    campayResponse.externalReference()
                );
            } else {
                LOGGER.error("Campay returned non-OK status: {}", response.getStatusCode());
                return new PaymentResponse(false, "Échec de l'initiation du paiement", null, null, "failed", null, null, null);
            }
        } catch (Exception e) {
            LOGGER.error("Exception during Campay request: {}", e.getMessage(), e);
            return new PaymentResponse(false, "Erreur lors de l'initiation du paiement: " + e.getMessage(), null, null, "failed", null, null, null);
        }
    }

    @Override
    public PaymentResponse verifyTransaction(String transactionId) {
        String url = campayBaseUrl + "/transaction/" + transactionId + "/";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Token " + campayApiKey);
        
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<CampayCollectResponse> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                entity, 
                CampayCollectResponse.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                CampayCollectResponse campayResponse = response.getBody();
                boolean isSuccess = "SUCCESSFUL".equals(campayResponse.status());
                
                // Mise à jour synchrone du plan si paiement réussi
                if (isSuccess) {
                    // Pour Campay, nous utilisons l'email stocké dans la référence externe ou récupéré via une autre méthode
                    // Ici nous allons chercher l'utilisateur par une autre méthode car Campay ne renvoie pas l'email directement
                    LOGGER.info("Paiement réussi pour la référence {}", transactionId);
                    // Note: La mise à jour du plan se fera via le webhook qui contient plus d'informations
                }
                
                return new PaymentResponse(
                    isSuccess,
                    "Transaction vérifiée: " + campayResponse.status(),
                    null,
                    campayResponse.reference(),
                    campayResponse.status(),
                    new BigDecimal(campayResponse.amount()),
                    campayResponse.currency(),
                    campayResponse.externalReference()
                );
            } else {
                return new PaymentResponse(false, "Échec de la vérification de la transaction", null, null, "failed", null, null, null);
            }
        } catch (Exception e) {
            return new PaymentResponse(false, "Erreur lors de la vérification: " + e.getMessage(), null, null, "failed", null, null, null);
        }
    }

    @Override
    @Transactional
    public void handleWebhook(Object webhookEvent) {
        if (webhookEvent instanceof CampayWebhookEvent campayEvent) {
            handleCampayWebhook(campayEvent);
        }
    }

    private void handleCampayWebhook(CampayWebhookEvent campayEvent) {
        try {
            if ("SUCCESSFUL".equals(campayEvent.status())) {
                handleSuccessfulCampayPayment(campayEvent);
            } else if ("FAILED".equals(campayEvent.status())) {
                handleFailedCampayPayment(campayEvent);
            }
        } catch (Exception e) {
            LOGGER.error("Erreur lors du traitement du webhook Campay: {}", e.getMessage());
        }
    }

    private void handleSuccessfulCampayPayment(CampayWebhookEvent campayEvent) {
        try {
            String email = campayEvent.customerEmail();
            LOGGER.info("Webhook Campay reçu - Email: {}, Référence: {}, Statut: {}", email, campayEvent.reference(), campayEvent.status());
            
            if (email == null || email.isEmpty()) {
                LOGGER.warn("Email non fourni dans le webhook Campay pour la référence {}", campayEvent.reference());
                return;
            }
            
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'email: " + email));
            
            LOGGER.info("Utilisateur trouvé - ID: {}, Email: {}, Plan actuel: {}", user.getId(), user.getEmail(), user.getPlan());
            
            // Mettre à jour uniquement si le plan n'est pas déjà "pro"
            if (!"pro".equals(user.getPlan())) {
                user.setPlan("pro");
                user.setMaxAddresses(PREMIUM_MAX_ADDRESSES);
                User savedUser = userRepository.save(user);
                LOGGER.info("Plan mis à jour pour l'utilisateur {} - Nouveau plan: {}, Max adresses: {}", email, savedUser.getPlan(), savedUser.getMaxAddresses());
            } else {
                LOGGER.info("Utilisateur {} a déjà le plan pro, pas de mise à jour nécessaire", email);
            }
            
            NotificationCreateRequest notificationRequest = new NotificationCreateRequest(
                "Abonnement activé",
                "Votre abonnement Pro a été activé avec succès. Vous pouvez maintenant créer jusqu'à 100 adresses.",
                "subscription",
                "/api/subscriptions/me"
            );
            notificationService.createNotification(notificationRequest, user.getId());
            
        } catch (Exception e) {
            LOGGER.error("Erreur lors du traitement du paiement réussi: {}", e.getMessage(), e);
        }
    }

    private void handleFailedCampayPayment(CampayWebhookEvent campayEvent) {
        try {
            String email = campayEvent.customerEmail();
            if (email == null || email.isEmpty()) {
                LOGGER.warn("Email non fourni dans le webhook Campay pour la référence {}", campayEvent.reference());
                return;
            }
            
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'email: " + email));
            
            NotificationCreateRequest notificationRequest = new NotificationCreateRequest(
                "Paiement échoué",
                "Votre paiement a échoué. Veuillez réessayer ou contacter le support.",
                "payment",
                "/api/subscriptions/upgrade"
            );
            notificationService.createNotification(notificationRequest, user.getId());
            
        } catch (Exception e) {
            LOGGER.error("Erreur lors du traitement du paiement échoué: {}", e.getMessage());
        }
    }
}