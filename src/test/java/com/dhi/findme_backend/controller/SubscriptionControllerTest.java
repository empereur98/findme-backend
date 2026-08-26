package com.dhi.findme_backend.controller;

import com.dhi.findme_backend.dto.PaymentResponse;
import com.dhi.findme_backend.dto.SubscriptionResponse;
import com.dhi.findme_backend.dto.SubscriptionUpgradeRequest;
import com.dhi.findme_backend.exception.BusinessException;
import com.dhi.findme_backend.exception.ResourceNotFoundException;
import com.dhi.findme_backend.security.SecurityUtilsInterface;
import com.dhi.findme_backend.service.SubscriptionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SubscriptionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SubscriptionService subscriptionService;

    @Mock
    private SecurityUtilsInterface securityUtils;

    @InjectMocks
    private SubscriptionController subscriptionController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private UUID userId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(subscriptionController)
                .setControllerAdvice(new com.dhi.findme_backend.exception.GlobalExceptionHandler())
                .build();

        userId = UUID.randomUUID();
        when(securityUtils.getCurrentUserId()).thenReturn(userId);
    }

    @Test
    void getSubscriptionStatus_whenUserExists_shouldReturnSubscription() throws Exception {
        SubscriptionResponse response = new SubscriptionResponse("free", 5, 2, null);
        when(subscriptionService.getSubscriptionStatus(userId)).thenReturn(response);

        mockMvc.perform(get("/api/subscriptions/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plan", is("free")))
                .andExpect(jsonPath("$.maxAddresses", is(5)));
    }

    @Test
    void upgradeSubscription_withValidRequest_shouldInitiatePayment() throws Exception {
        SubscriptionUpgradeRequest request = new SubscriptionUpgradeRequest("card", "771234567", "+221", "pro");
        PaymentResponse paymentResponse = new PaymentResponse(true, "Success", "http://payment.link", "tx123", "pending", null, null, null);
        when(subscriptionService.upgradeSubscription(any(SubscriptionUpgradeRequest.class), eq(userId))).thenReturn(paymentResponse);

        mockMvc.perform(post("/api/subscriptions/upgrade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentLink", is("http://payment.link")));
    }

    @Test
    void upgradeSubscription_whenAlreadyPro_shouldReturnBadRequest() throws Exception {
        SubscriptionUpgradeRequest request = new SubscriptionUpgradeRequest("card", "771234567", "+221", "pro");
        when(subscriptionService.upgradeSubscription(any(SubscriptionUpgradeRequest.class), eq(userId)))
                .thenThrow(new BusinessException("ALREADY_SUBSCRIBED", "User is already on this plan"));

        mockMvc.perform(post("/api/subscriptions/upgrade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("ALREADY_SUBSCRIBED")));
    }

    @Test
    void upgradeSubscription_withInvalidRequest_shouldReturnUnprocessableEntity() throws Exception {
        String invalidRequest = "{\"paymentMethod\":\"\", \"phone\":\"771234567\", \"countryCode\":\"+221\", \"plan\":\"pro\"}";

        mockMvc.perform(post("/api/subscriptions/upgrade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void cancelSubscription_whenUserHasSubscription_shouldCancel() throws Exception {
        mockMvc.perform(delete("/api/subscriptions/me"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getSubscriptionStatus_whenUserNotFound_shouldReturnNotFound() throws Exception {
        when(subscriptionService.getSubscriptionStatus(userId))
                .thenThrow(new ResourceNotFoundException("USER_NOT_FOUND", "User not found"));

        mockMvc.perform(get("/api/subscriptions/me"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", is("USER_NOT_FOUND")));
    }
}