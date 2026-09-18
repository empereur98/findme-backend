package com.dhi.findme_backend.integration.config;

import com.dhi.findme_backend.config.*;
import com.dhi.findme_backend.security.*;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpenApiConfigTest {

    private final OpenApiConfig openApiConfig = new OpenApiConfig();

    @Test
    void findmeOpenAPI_shouldBeConfiguredCorrectly() {
        OpenAPI openAPI = openApiConfig.findmeOpenAPI();

        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());
        assertEquals("FindMe API", openAPI.getInfo().getTitle());
        assertEquals("v1.0.0", openAPI.getInfo().getVersion());
        assertEquals("contact@findme.com", openAPI.getInfo().getContact().getEmail());

        assertNotNull(openAPI.getComponents());
        assertTrue(openAPI.getComponents().getSecuritySchemes().containsKey("Bearer Authentication"));

        SecurityScheme scheme = openAPI.getComponents().getSecuritySchemes().get("Bearer Authentication");
        assertEquals(SecurityScheme.Type.HTTP, scheme.getType());
        assertEquals("bearer", scheme.getScheme());
        assertEquals("JWT", scheme.getBearerFormat());

        assertNotNull(openAPI.getServers());
        assertFalse(openAPI.getServers().isEmpty());
    }
}

