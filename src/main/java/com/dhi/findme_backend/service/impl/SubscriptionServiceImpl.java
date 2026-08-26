package com.dhi.findme_backend.service.impl;

import com.dhi.findme_backend.dto.Features;
import com.dhi.findme_backend.dto.PaymentRequest;
import com.dhi.findme_backend.dto.PaymentResponse;
import com.dhi.findme_backend.dto.SubscriptionResponse;
import com.dhi.findme_backend.dto.SubscriptionUpgradeRequest;
import com.dhi.findme_backend.entity.User;
import com.dhi.findme_backend.exception.ResourceNotFoundException;
import com.dhi.findme_backend.repository.UserRepository;
import com.dhi.findme_backend.service.PaymentService;
import com.dhi.findme_backend.service.SubscriptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {

    private static final int DEFAULT_MAX_ADDRESSES = 4;

    private final UserRepository userRepository;
    private final PaymentService paymentService;

    public SubscriptionServiceImpl(UserRepository userRepository, PaymentService paymentService) {
        this.userRepository = userRepository;
        this.paymentService = paymentService;
    }

    @Override
    public PaymentResponse upgradeSubscription(SubscriptionUpgradeRequest request, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("USER_NOT_FOUND", "Utilisateur non trouvé"));

        // Créer une demande de paiement
        PaymentRequest paymentRequest = new PaymentRequest(
            new BigDecimal("10.00"),
            "USD",
            user.getEmail(),
            user.getPhone(),
            user.getFirstName() + " " + user.getLastName(),
            "tx-ref-" + UUID.randomUUID(),
            "https://findme-app.com/payment-success",
            request.paymentMethod()
        );

        // Initier le paiement via le service de paiement
        return paymentService.initiatePayment(paymentRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionResponse getSubscriptionStatus(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("USER_NOT_FOUND", "Utilisateur non trouvé"));

        boolean isPro = "pro".equals(user.getPlan());
        return new SubscriptionResponse(
            user.getPlan(),
            user.getMaxAddresses(),
            user.getAddressesCreatedCount(),
            new Features(isPro, isPro, isPro, isPro)
        );
    }

    @Override
    public void cancelSubscription(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("USER_NOT_FOUND", "Utilisateur non trouvé"));

        // TODO: Implémenter la logique d'annulation avec fin de période
        user.setPlan("free");
        user.setMaxAddresses(DEFAULT_MAX_ADDRESSES);
        userRepository.save(user);
    }
}