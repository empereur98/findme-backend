package com.dhi.findme_backend.e2e;

import com.dhi.findme_backend.dto.*;
import com.dhi.findme_backend.entity.*;
import com.dhi.findme_backend.repository.*;
import com.dhi.findme_backend.service.*;
import com.dhi.findme_backend.dto.AuthLoginRequest;
import com.dhi.findme_backend.dto.AuthRegisterRequest;
import com.dhi.findme_backend.dto.PasswordChangeRequest;
import com.dhi.findme_backend.dto.UserUpdateRequest;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthAndUserFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void completeAuthAndUserLifecycle_shouldSucceed() throws Exception {
        String uniqueEmail = "flow.user." + UUID.randomUUID() + "@example.com";
        String initialPassword = "Password123!";
        String newPassword = "NewPassword123!";

        // 1. Inscription
        AuthRegisterRequest registerRequest = new AuthRegisterRequest(
                "Flow", "User", uniqueEmail, initialPassword
        );

        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is(201)))
                .andExpect(jsonPath("$.data.user.email", is(uniqueEmail)))
                .andReturn();

        JsonNode registerNode = objectMapper.readTree(registerResult.getResponse().getContentAsString());
        String initialToken = registerNode.get("data").get("token").asText();
        assertNotNull(initialToken);

        // 2. Connexion
        AuthLoginRequest loginRequest = new AuthLoginRequest(uniqueEmail, initialPassword);
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.data.user.email", is(uniqueEmail)))
                .andReturn();

        JsonNode loginNode = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String authToken = loginNode.get("data").get("token").asText();
        assertNotNull(authToken);

        // 3. Consultation du profil
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.data.email", is(uniqueEmail)))
                .andExpect(jsonPath("$.data.firstName", is("Flow")))
                .andExpect(jsonPath("$.data.lastName", is("User")));

        // 4. Mise Ã  jour du profil (via PATCH /api/users/me)
        UserUpdateRequest updateRequest = new UserUpdateRequest("UpdatedFlow", "UpdatedUser", "+237697000222", "Douala", null);
        mockMvc.perform(patch("/api/users/me")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.data.firstName", is("UpdatedFlow")))
                .andExpect(jsonPath("$.data.lastName", is("UpdatedUser")));

        // 5. Changement de mot de passe (via PUT /api/users/me/password)
        PasswordChangeRequest changePasswordRequest = new PasswordChangeRequest(initialPassword, newPassword);
        mockMvc.perform(put("/api/users/me/password")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)));

        // 6. Tentative de connexion avec l'ancien mot de passe -> Doit Ã©chouer (401 Unauthorized)
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AuthLoginRequest(uniqueEmail, initialPassword))))
                .andExpect(status().isUnauthorized());

        // 7. Connexion avec le nouveau mot de passe -> Doit rÃ©ussir (200)
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AuthLoginRequest(uniqueEmail, newPassword))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)));
    }
}

