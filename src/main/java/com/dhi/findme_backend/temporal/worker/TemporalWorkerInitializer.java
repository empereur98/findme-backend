/*
package com.dhi.findme_backend.temporal.worker;

import com.dhi.findme_backend.temporal.activity.AddressActivity;
import com.dhi.findme_backend.temporal.activity.impl.AddressActivityImpl;
import com.dhi.findme_backend.temporal.workflow.AddressWorkflow;
import com.dhi.findme_backend.temporal.workflow.impl.AddressWorkflowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Component
public class TemporalWorkerInitializer {

    private static final Logger logger = LoggerFactory.getLogger(TemporalWorkerInitializer.class);
    private final WorkflowServiceStubs service;
    private final WorkflowClient client;
    private final AddressActivity addressActivity;
    private WorkerFactory factory;

    public TemporalWorkerInitializer(WorkflowServiceStubs service, WorkflowClient client, AddressActivity addressActivity) {
        this.service = service;
        this.client = client;
        this.addressActivity = addressActivity;
    }

    @PostConstruct
    public void start() {
        factory = WorkerFactory.newInstance(client);
        Worker worker = factory.newWorker("address-processing-queue");
        worker.registerWorkflowImplementationTypes(AddressWorkflowImpl.class);
        worker.registerActivitiesImplementations(addressActivity);
        factory.start();
        logger.info("Temporal worker started.");
    }

    @PreDestroy
    public void stop() {
        if (factory != null) {
            factory.shutdown();
            logger.info("Temporal worker stopped.");
        }
    }
}
*/