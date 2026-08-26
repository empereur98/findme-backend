package com.dhi.findme_backend.service;

import com.dhi.findme_backend.dto.PaymentResponse;
import com.dhi.findme_backend.dto.SubscriptionResponse;
import com.dhi.findme_backend.dto.SubscriptionUpgradeRequest;

import java.util.UUID;

public interface SubscriptionService {

    PaymentResponse upgradeSubscription(SubscriptionUpgradeRequest request, UUID userId);

    SubscriptionResponse getSubscriptionStatus(UUID userId);

    void cancelSubscription(UUID userId);
}