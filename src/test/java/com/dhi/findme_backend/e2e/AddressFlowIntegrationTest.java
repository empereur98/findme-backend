package com.dhi.findme_backend.e2e;

import com.dhi.findme_backend.dto.*;
import com.dhi.findme_backend.entity.*;
import com.dhi.findme_backend.repository.*;
import com.dhi.findme_backend.service.*;
import com.dhi.findme_backend.dto.AddressCreateRequest;
import com.dhi.findme_backend.dto.AddressUpdateRequest;
import com.dhi.findme_backend.dto.AuthRegisterRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AddressFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        String email = "addr.user." + UUID.randomUUID() + "@example.com";
        AuthRegisterRequest registerRequest = new AuthRegisterRequest(
                "Address", "Owner", email, "Password123!"
        );
        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(registerResult.getResponse().getContentAsString());
        authToken = jsonNode.get("data").get("token").asText();
    }

    @Test
    void completeAddressLifecycle_createReadUpdateDelete_shouldSucceed() throws Exception {
        // 1. CrÃ©ation d'une adresse
        AddressCreateRequest createRequest = new AddressCreateRequest(
                "Maison Principale", "cm", "Douala", "Bonapriso",
                "Rue des Palmiers", "En face de la pharmacie", 4.051056, 9.767868,
                null, "Personnel"
        );

        MvcResult createResult = mockMvc.perform(post("/api/addresses")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is(201)))
                .andExpect(jsonPath("$.data.name", is("Maison Principale")))
                .andExpect(jsonPath("$.data.city", is("Douala")))
                .andReturn();

        JsonNode createNode = objectMapper.readTree(createResult.getResponse().getContentAsString());
        String addressId = createNode.get("data").get("id").asText();

        // 2. Consultation par ID
        mockMvc.perform(get("/api/addresses/" + addressId)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.data.id", is(addressId)))
                .andExpect(jsonPath("$.data.district", is("Bonapriso")));

        // 3. Mise Ã  jour de l'adresse
        AddressUpdateRequest updateRequest = new AddressUpdateRequest(
                "Maison RÃ©novÃ©e", "Bonanjo", "Avenue Charles de Gaulle",
                "PrÃ¨s du consulat", 4.052000, 9.768000, null, "Personnel"
        );

        mockMvc.perform(put("/api/addresses/" + addressId)
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.data.name", is("Maison RÃ©novÃ©e")))
                .andExpect(jsonPath("$.data.district", is("Bonanjo")));

        // 4. Suppression de l'adresse
        mockMvc.perform(delete("/api/addresses/" + addressId)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)));

        // 5. VÃ©rification que l'adresse n'existe plus (404)
        mockMvc.perform(get("/api/addresses/" + addressId)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNotFound());
    }
}

