package com.dhi.findme_backend.unit.controller;

import com.dhi.findme_backend.controller.*;
import com.dhi.findme_backend.service.*;
import com.dhi.findme_backend.dto.*;
import com.dhi.findme_backend.entity.*;
import com.dhi.findme_backend.exception.*;
import com.dhi.findme_backend.security.*;
import com.dhi.findme_backend.dto.InvoiceResponse;
import com.dhi.findme_backend.exception.GlobalExceptionHandler;
import com.dhi.findme_backend.security.SecurityUtils;
import com.dhi.findme_backend.service.BillingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BillingControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BillingService billingService;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private BillingController billingController;

    private UUID userId;
    private InvoiceResponse invoiceResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(billingController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        userId = UUID.randomUUID();
        invoiceResponse = new InvoiceResponse(
                UUID.randomUUID(),
                "INV-2026-001",
                BigDecimal.valueOf(15000),
                LocalDate.now(),
                LocalDate.now().plusDays(30),
                "PAID",
                "Abonnement Pro Mensuel"
        );
    }

    @Test
    void getInvoices_whenAuthenticated_shouldReturnInvoicesPage() throws Exception {
        when(securityUtils.getCurrentUserId()).thenReturn(userId);
        Page<InvoiceResponse> page = new PageImpl<>(List.of(invoiceResponse), PageRequest.of(0, 10), 1);
        when(billingService.getInvoicesForUser(eq(userId), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/billing/invoices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].invoiceNumber", is("INV-2026-001")))
                .andExpect(jsonPath("$.data.content[0].status", is("PAID")));

        verify(billingService).getInvoicesForUser(eq(userId), any(Pageable.class));
    }

    @Test
    void getInvoices_whenNoInvoices_shouldReturnEmptyPage() throws Exception {
        when(securityUtils.getCurrentUserId()).thenReturn(userId);
        Page<InvoiceResponse> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(billingService.getInvoicesForUser(eq(userId), any(Pageable.class))).thenReturn(emptyPage);

        mockMvc.perform(get("/api/billing/invoices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.data.content", hasSize(0)));
    }
}

