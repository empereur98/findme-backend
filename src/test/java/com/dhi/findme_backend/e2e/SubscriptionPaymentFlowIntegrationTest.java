package com.dhi.findme_backend.e2e;

import com.dhi.findme_backend.dto.*;
import com.dhi.findme_backend.entity.*;
import com.dhi.findme_backend.repository.*;
import com.dhi.findme_backend.service.*;
import com.dhi.findme_backend.dto.AuthRegisterRequest;
import com.dhi.findme_backend.dto.CampayWebhookEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SubscriptionPaymentFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void userUpgradeFlowViaCampayWebhook_shouldUpdateUserPlanToPro() throws Exception {
        String email = "subscriber." + UUID.randomUUID() + "@example.com";

        // 1. Inscription utilisateur
        AuthRegisterRequest registerRequest = new AuthRegisterRequest(
                "Subscriber", "Test", email, "Password123!"
        );
        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(registerResult.getResponse().getContentAsString());
        String token = jsonNode.get("data").get("token").asText();

        // 2. VÃ©rifier que l'utilisateur commence avec le plan "free"
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.plan", is("free")))
                .andExpect(jsonPath("$.data.maxAddresses", is(4)));

        // 3. Simuler la rÃ©ception d'un webhook Campay de paiement rÃ©ussi pour cet utilisateur
        CampayWebhookEvent successEvent = new CampayWebhookEvent(
                "REF-" + System.currentTimeMillis(),
                "EXT-REF-123",
                "SUCCESSFUL",
                "10000",
                "XAF",
                "MTN",
                "00",
                "OP-456",
                "+237697000444",
                email
        );

        mockMvc.perform(post("/api/payments/webhook/campay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(successEvent)))
                .andExpect(status().isOk());

        // 4. VÃ©rifier que l'utilisateur est maintenant sur le plan "pro" avec limite 100
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.plan", is("pro")))
                .andExpect(jsonPath("$.data.maxAddresses", is(100)));
    }
}

