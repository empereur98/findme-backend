package com.dhi.findme_backend.service.impl;

import com.dhi.findme_backend.dto.InvoiceResponse;
import com.dhi.findme_backend.mapper.InvoiceMapper;
import com.dhi.findme_backend.repository.InvoiceRepository;
import com.dhi.findme_backend.service.BillingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class BillingServiceImpl implements BillingService {

    private static final int DUE_DATE_DAYS = 15;

    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;

    public BillingServiceImpl(InvoiceRepository invoiceRepository, InvoiceMapper invoiceMapper) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceMapper = invoiceMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InvoiceResponse> getInvoicesForUser(UUID userId, Pageable pageable) {
        // Pour l'instant, nous retournons des données factices.
        // Plus tard, nous remplacerons cela par une vraie recherche dans la base de données.
        // Page<Invoice> invoices = invoiceRepository.findByUserId(userId, pageable);
        // return invoices.map(invoiceMapper::toInvoiceResponse);

        InvoiceResponse dummyInvoice = new InvoiceResponse(
            UUID.randomUUID(),
            "INV-2024-001",
            new BigDecimal("29.99"),
            LocalDate.now().minusMonths(1),
            LocalDate.now().minusMonths(1).plusDays(DUE_DATE_DAYS),
            "PAID",
            "Abonnement Premium - Mensuel"
        );

        List<InvoiceResponse> dummyList = Collections.singletonList(dummyInvoice);
        return new PageImpl<>(dummyList, pageable, dummyList.size());
    }
}