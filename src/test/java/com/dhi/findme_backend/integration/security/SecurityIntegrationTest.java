package com.dhi.findme_backend.integration.security;

import com.dhi.findme_backend.security.*;
import com.dhi.findme_backend.entity.*;
import com.dhi.findme_backend.repository.*;
import com.dhi.findme_backend.config.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void publicEndpoints_shouldBeAccessibleWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/geo/countries"))
                .andExpect(status().isOk());
    }

    @Test
    void protectedEndpoints_withoutAuthentication_shouldBeForbiddenOrUnauthorized() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/settings"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/billing/invoices"))
                .andExpect(status().isForbidden());
    }

    @Test
    void protectedEndpoints_withInvalidBearerToken_shouldBeForbidden() throws Exception {
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer this.is.an.invalid.token"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void adminEndpoints_withUserRole_shouldBeForbidden() throws Exception {
        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/settings"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminEndpoints_withAdminRole_shouldBeAccessible() throws Exception {
        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isOk());
    }
}

