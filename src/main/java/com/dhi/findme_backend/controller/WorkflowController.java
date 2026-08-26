/*
package com.dhi.findme_backend.controller;

import com.dhi.findme_backend.dto.AddressCreateRequest;
import com.dhi.findme_backend.dto.AddressResponse;
import com.dhi.findme_backend.temporal.workflow.AddressWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/workflows")
public class WorkflowController {

    @Autowired
    private WorkflowClient workflowClient;

    @PostMapping("/addresses/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> createAddressWorkflow(
            @RequestBody AddressCreateRequest request,
            @RequestParam UUID userId) {
        
        String workflowId = "address-" + UUID.randomUUID();
        
        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setTaskQueue("ADDRESS_TASK_QUEUE")
                .setWorkflowId(workflowId)
                .build();
        
        AddressWorkflow workflow = workflowClient.newWorkflowStub(AddressWorkflow.class, options);
        // workflow.createAddress(request, userId); // This method does not exist
        
        return ResponseEntity.ok(workflowId);
    }

    @GetMapping("/addresses/{workflowId}/result")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AddressResponse> getAddressWorkflowResult(
            @PathVariable String workflowId) {
        
        AddressWorkflow workflow = workflowClient.newWorkflowStub(AddressWorkflow.class, workflowId);
        // AddressResponse result = workflow.createAddress(null, null); // This method does not exist
        
        return ResponseEntity.ok(null);
    }

    @GetMapping("/addresses/{workflowId}/status")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> getAddressWorkflowStatus(
            @PathVariable String workflowId) {
        
        // Pour l'instant, retourne un statut simple
        // Dans une implémentation complète, on utiliserait WorkflowClient.describeWorkflowExecution
        return ResponseEntity.ok("RUNNING");
    }
}
*/