package com.dhi.findme_backend.service.impl;

import com.dhi.findme_backend.dto.PaymentRequest;
import com.dhi.findme_backend.dto.PaymentResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CampayIntegrationTest {

    @Value("${campay.api-key}")
    private String campayApiKey;

    @Value("${campay.base-url}")
    private String campayBaseUrl;

    @Test
    public void testCampayApiConnection() {
        RestTemplate restTemplate = new RestTemplate();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Token " + campayApiKey);
        
        String url = campayBaseUrl + "/collect/";
        
        CampayCollectRequest campayRequest = new CampayCollectRequest(
            new BigDecimal("100"),
            "XAF",
            "+237697560705",
            "Test paiement",
            "TEST-REF-001"
        );
        
        HttpEntity<CampayCollectRequest> entity = new HttpEntity<>(campayRequest, headers);
        
        try {
            CampayCollectResponse response = restTemplate.exchange(
                url, 
                HttpMethod.POST, 
                entity, 
                CampayCollectResponse.class
            ).getBody();
            
            assertNotNull(response);
            assertNotNull(response.reference());
            System.out.println("Campay API Response: " + response);
            
        } catch (Exception e) {
            System.out.println("Erreur lors du test Campay: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private record CampayCollectRequest(
        java.math.BigDecimal amount,
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
