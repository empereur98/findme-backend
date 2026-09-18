package com.dhi.findme_backend.integration.config;

import com.dhi.findme_backend.config.*;
import com.dhi.findme_backend.security.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class ApplicationPropertiesTest {

    @Value("${spring.application.name:findme-backend}")
    private String applicationName;

    @Value("${campay.base-url:https://demo.campay.net/api}")
    private String campayBaseUrl;

    @Value("${pagination.default-page-size:20}")
    private int defaultPageSize;

    @Value("${pagination.max-page-size:100}")
    private int maxPageSize;

    @Test
    void testApplicationProperties_shouldBeLoadedProperly() {
        assertNotNull(applicationName);
        assertFalse(applicationName.isBlank());

        assertNotNull(campayBaseUrl);
        assertFalse(campayBaseUrl.isBlank());

        org.junit.jupiter.api.Assertions.assertTrue(defaultPageSize > 0);
        org.junit.jupiter.api.Assertions.assertTrue(maxPageSize >= defaultPageSize);
    }
}

