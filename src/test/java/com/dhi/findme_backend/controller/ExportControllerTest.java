package com.dhi.findme_backend.controller;

import com.dhi.findme_backend.dto.ExportResponse;
import com.dhi.findme_backend.exception.BusinessException;
import com.dhi.findme_backend.security.SecurityUtilsInterface;
import com.dhi.findme_backend.service.ExportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ExportControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ExportService exportService;

    @Mock
    private SecurityUtilsInterface securityUtils;

    @InjectMocks
    private ExportController exportController;

    private UUID userId;
    private UUID addressId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(exportController)
                .setControllerAdvice(new com.dhi.findme_backend.exception.GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        userId = UUID.randomUUID();
        addressId = UUID.randomUUID();
        when(securityUtils.getCurrentUserId()).thenReturn(userId);
    }

    @Test
    void generatePdfExport_whenAllowed_shouldSucceed() throws Exception {
        ExportResponse exportResponse = new ExportResponse(UUID.randomUUID().toString(), "export.pdf", "url", null, null);
        when(exportService.generatePdfExport(addressId, userId)).thenReturn(exportResponse);

        mockMvc.perform(post("/api/exports/pdf/" + addressId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.filename", is("export.pdf")));
    }

    @Test
    void generatePdfExport_whenPlanNotAllowed_shouldReturnForbidden() throws Exception {
        when(exportService.generatePdfExport(addressId, userId))
                .thenThrow(new BusinessException("PRO_PLAN_REQUIRED", "Pro plan required"));

        mockMvc.perform(post("/api/exports/pdf/" + addressId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("PRO_PLAN_REQUIRED")));
    }

    @Test
    void getExportHistory_whenAuthenticated_shouldReturnPageOfExports() throws Exception {
        ExportResponse exportResponse = new ExportResponse(UUID.randomUUID().toString(), "export.pdf", "url", null, null);
        when(exportService.getExportHistory(any(Pageable.class), eq(userId)))
                .thenReturn(new PageImpl<>(Collections.singletonList(exportResponse), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/exports/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].filename", is("export.pdf")));
    }
}