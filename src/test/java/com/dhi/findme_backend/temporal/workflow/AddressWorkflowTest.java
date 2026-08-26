/*package com.dhi.findme_backend.temporal.workflow;

import com.dhi.findme_backend.dto.AddressCreateRequest;
import com.dhi.findme_backend.dto.AddressResponse;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AddressWorkflowTest {

    @Test
    void testAddressWorkflowInterface_ShouldBeDefined() {
        // Test simple pour vérifier que l'interface est bien définie
        assertNotNull(AddressWorkflow.class);
    }

    @Test
    void testAddressWorkflowMethods_ShouldBeDefined() {
        // Test pour vérifier que les méthodes sont bien définies
        try {
            AddressWorkflow.class.getMethod("createAddress", AddressCreateRequest.class, UUID.class);
        } catch (NoSuchMethodException e) {
            fail("Les méthodes de workflow ne sont pas correctement définies");
        }
    }
}
*/