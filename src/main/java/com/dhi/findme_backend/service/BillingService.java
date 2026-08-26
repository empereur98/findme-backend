package com.dhi.findme_backend.service;

import com.dhi.findme_backend.dto.InvoiceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BillingService {
    Page<InvoiceResponse> getInvoicesForUser(UUID userId, Pageable pageable);
}