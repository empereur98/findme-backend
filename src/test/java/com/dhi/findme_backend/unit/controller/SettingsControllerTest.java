package com.dhi.findme_backend.unit.controller;

import com.dhi.findme_backend.controller.*;
import com.dhi.findme_backend.service.*;
import com.dhi.findme_backend.dto.*;
import com.dhi.findme_backend.entity.*;
import com.dhi.findme_backend.exception.*;
import com.dhi.findme_backend.security.*;
import com.dhi.findme_backend.dto.SettingsRequest;
import com.dhi.findme_backend.dto.SettingsResponse;
import com.dhi.findme_backend.exception.GlobalExceptionHandler;
import com.dhi.findme_backend.service.SettingsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SettingsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SettingsService settingsService;

    @InjectMocks
    private SettingsController settingsController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private SettingsResponse settingsResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(settingsController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        settingsResponse = new SettingsResponse(true, false, 80, 100, 30);
    }

    @Test
    void getSettings_shouldReturnSettings() throws Exception {
        when(settingsService.getSettings()).thenReturn(settingsResponse);

        mockMvc.perform(get("/api/settings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.data.strictFormatting", is(true)))
                .andExpect(jsonPath("$.data.autoCorrect", is(false)))
                .andExpect(jsonPath("$.data.validationThreshold", is(80)))
                .andExpect(jsonPath("$.data.rateLimit", is(100)))
                .andExpect(jsonPath("$.data.webhookTimeout", is(30)));

        verify(settingsService).getSettings();
    }

    @Test
    void updateSettings_withValidPayload_shouldReturnUpdatedSettings() throws Exception {
        SettingsRequest request = new SettingsRequest(false, true, 90, 200, 60);
        SettingsResponse updatedResponse = new SettingsResponse(false, true, 90, 200, 60);
        when(settingsService.updateSettings(any(SettingsRequest.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/settings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.data.strictFormatting", is(false)))
                .andExpect(jsonPath("$.data.autoCorrect", is(true)))
                .andExpect(jsonPath("$.data.validationThreshold", is(90)));

        verify(settingsService).updateSettings(any(SettingsRequest.class));
    }
}

