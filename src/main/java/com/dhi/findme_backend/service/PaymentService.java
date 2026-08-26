package com.dhi.findme_backend.service;

import com.dhi.findme_backend.dto.PaymentRequest;
import com.dhi.findme_backend.dto.PaymentResponse;

public interface PaymentService {

    PaymentResponse initiatePayment(PaymentRequest request);

    PaymentResponse verifyTransaction(String transactionId);

    void handleWebhook(Object webhookEvent);
}
