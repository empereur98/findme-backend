package com.dhi.findme_backend.integration.service;

import com.dhi.findme_backend.service.*;
import com.dhi.findme_backend.service.impl.*;
import com.dhi.findme_backend.entity.*;
import com.dhi.findme_backend.dto.*;
import com.dhi.findme_backend.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test d'intÃ©gration rÃ©el contre l'API Campay.
 *
 * Il effectue un appel HTTP sortant et dÃ©clenche un vrai paiement de test : il est donc
 * dÃ©sactivÃ© par dÃ©faut et n'est exÃ©cutÃ© que si la variable d'environnement
 * {@code CAMPAY_INTEGRATION_TEST=true} est dÃ©finie, avec des identifiants Campay valides.
 */
@SpringBootTest
@ActiveProfiles("test")
@EnabledIfEnvironmentVariable(named = "CAMPAY_INTEGRATION_TEST", matches = "true")
class CampayIntegrationTest {

    @Value("${campay.api-key:}")
    private String campayApiKey;

    @Value("${campay.base-url:}")
    private String campayBaseUrl;

    @Value("${campay.test-phone:}")
    private String campayTestPhone;

    @Test
    void collect_withValidCredentials_returnsReference() {
        assertFalse(campayApiKey.isBlank(), "campay.api-key doit Ãªtre configurÃ©e");
        assertFalse(campayBaseUrl.isBlank(), "campay.base-url doit Ãªtre configurÃ©e");
        assertFalse(campayTestPhone.isBlank(), "campay.test-phone doit Ãªtre configurÃ©e");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Token " + campayApiKey);

        CampayCollectRequest request = new CampayCollectRequest(
                new BigDecimal("100"),
                "XAF",
                campayTestPhone,
                "Test paiement",
                "TEST-REF-001"
        );

        CampayCollectResponse response = new RestTemplate().exchange(
                campayBaseUrl + "/collect/",
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                CampayCollectResponse.class
        ).getBody();

        assertNotNull(response);
        assertNotNull(response.reference());
    }

    private record CampayCollectRequest(
            BigDecimal amount,
            String currency,
            String from,
            String description,
            String external_reference
    ) {}

    private record CampayCollectResponse(
            String reference,
            String external_reference,
            String status,
            String amount,
            String currency,
            String operator,
            String code,
            String operator_reference,
            String ussd_code
    ) {}
}

