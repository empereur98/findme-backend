package com.dhi.findme_backend.unit.controller;

import com.dhi.findme_backend.controller.*;
import com.dhi.findme_backend.service.*;
import com.dhi.findme_backend.dto.*;
import com.dhi.findme_backend.entity.*;
import com.dhi.findme_backend.exception.*;
import com.dhi.findme_backend.security.*;
import com.dhi.findme_backend.dto.CampayWebhookEvent;
import com.dhi.findme_backend.dto.PaymentRequest;
import com.dhi.findme_backend.dto.PaymentResponse;
import com.dhi.findme_backend.exception.GlobalExceptionHandler;
import com.dhi.findme_backend.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void initiatePayment_withValidRequest_shouldReturnPaymentResponse() throws Exception {
        PaymentRequest request = new PaymentRequest(
                BigDecimal.valueOf(10000), "XAF", "user@example.com",
                "+237697000000", "John Doe", "TX-123456", "https://app.findme.com/success", "MOMO"
        );
        PaymentResponse response = new PaymentResponse(
                true, "Paiement initiÃ©", "https://campay.net/pay/123",
                "TX-123456", "PENDING", BigDecimal.valueOf(10000), "XAF", "TX-123456"
        );
        when(paymentService.initiatePayment(any(PaymentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/payments/initiate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.data.success", is(true)))
                .andExpect(jsonPath("$.data.paymentLink", is("https://campay.net/pay/123")))
                .andExpect(jsonPath("$.data.transactionId", is("TX-123456")));

        verify(paymentService).initiatePayment(any(PaymentRequest.class));
    }

    @Test
    void verifyTransaction_shouldReturnTransactionStatus() throws Exception {
        PaymentResponse response = new PaymentResponse(
                true, "Transaction vÃ©rifiÃ©e", null,
                "TX-123456", "SUCCESSFUL", BigDecimal.valueOf(10000), "XAF", "TX-123456"
        );
        when(paymentService.verifyTransaction(eq("TX-123456"))).thenReturn(response);

        mockMvc.perform(get("/api/payments/verify/TX-123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.data.status", is("SUCCESSFUL")));

        verify(paymentService).verifyTransaction("TX-123456");
    }

    @Test
    void handleCampayWebhook_shouldProcessWebhook() throws Exception {
        CampayWebhookEvent event = new CampayWebhookEvent(
                "REF-001", "TX-123456", "SUCCESSFUL", "10000",
                "XAF", "MTN", "00", "OP-999", "+237697000000", "user@example.com"
        );

        mockMvc.perform(post("/api/payments/webhook/campay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("Webhook traité avec succès")));

        verify(paymentService).handleWebhook(any(CampayWebhookEvent.class));
    }

    @Test
    void testCampayWebhook_shouldSimulateWebhook() throws Exception {
        mockMvc.perform(post("/api/payments/test/webhook/campay")
                        .param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("Webhook simulé avec succès")));

        verify(paymentService).handleWebhook(any(CampayWebhookEvent.class));
    }
}

