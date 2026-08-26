import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class CampayApiTest {
    
    private static final String CAMPAY_API_KEY = "GK66oZeJbHeUdW4_b46m6_hcrThwNtVo6bGt8NwaSP62jmgmvZZuJmOZ8YGaScqekpEET2oIIKXLLVFTvzua4w";
    private static final String CAMPAY_BASE_URL = "https://demo.campay.net/api";
    
    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Token " + CAMPAY_API_KEY);
        
        String url = CAMPAY_BASE_URL + "/collect/";
        
        CampayCollectRequest campayRequest = new CampayCollectRequest(
            new BigDecimal("100"),
            "XAF",
            "+237697560705",
            "Test paiement FindMe",
            "TEST-REF-001"
        );
        
        HttpEntity<CampayCollectRequest> entity = new HttpEntity<>(campayRequest, headers);
        
        try {
            ResponseEntity<CampayCollectResponse> response = restTemplate.exchange(
                url, 
                HttpMethod.POST, 
                entity, 
                CampayCollectResponse.class
            );
            
            System.out.println("Status Code: " + response.getStatusCode());
            System.out.println("Response: " + response.getBody());
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                CampayCollectResponse campayResponse = response.getBody();
                System.out.println("✅ Campay API fonctionne correctement!");
                System.out.println("Reference: " + campayResponse.reference());
                System.out.println("Status: " + campayResponse.status());
                System.out.println("USSD Code: " + campayResponse.ussd_code());
            } else {
                System.out.println("❌ Erreur dans la réponse Campay");
            }
            
        } catch (Exception e) {
            System.out.println("❌ Erreur lors du test Campay: " + e.getMessage());
            e.printStackTrace();
        }
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
