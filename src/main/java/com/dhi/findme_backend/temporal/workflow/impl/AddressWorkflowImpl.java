/*
package com.dhi.findme_backend.temporal.workflow.impl;

import com.dhi.findme_backend.entity.Address;
import com.dhi.findme_backend.temporal.activity.AddressActivity;
import com.dhi.findme_backend.temporal.workflow.AddressWorkflow;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;

public class AddressWorkflowImpl implements AddressWorkflow {

    private final AddressActivity addressActivity = Workflow.newActivityStub(
            AddressActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(10))
                    .setRetryOptions(RetryOptions.newBuilder()
                            .setInitialInterval(Duration.ofSeconds(1))
                            .setMaximumAttempts(3)
                            .build())
                    .build()
    );

    @Override
    public void processAddress(Address address) {
        addressActivity.generateCodePlus(address);
        addressActivity.notifyUser(address);
    }
}
*/