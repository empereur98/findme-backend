package com.dhi.findme_backend.service;

import com.dhi.findme_backend.dto.SettingsRequest;
import com.dhi.findme_backend.dto.SettingsResponse;
import com.dhi.findme_backend.entity.Settings;
import com.dhi.findme_backend.exception.ResourceNotFoundException;
import com.dhi.findme_backend.mapper.SettingsMapper;
import com.dhi.findme_backend.repository.SettingsRepository;
import com.dhi.findme_backend.service.impl.SettingsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SettingsServiceTest {

    @Mock
    private SettingsRepository settingsRepository;

    @Mock
    private SettingsMapper settingsMapper;

    @InjectMocks
    private SettingsServiceImpl settingsService;

    private Settings testSettings;
    private SettingsResponse settingsResponse;
    private SettingsRequest settingsRequest;

    @BeforeEach
    void setUp() {
        testSettings = new Settings();
        testSettings.setStrictFormatting(true);
        testSettings.setAutoCorrect(false);
        testSettings.setValidationThreshold(80);
        testSettings.setRateLimit(100);
        testSettings.setWebhookTimeout(30);

        settingsResponse = new SettingsResponse(
                true,
                false,
                80,
                100,
                30
        );

        settingsRequest = new SettingsRequest(
                false,
                true,
                90,
                150,
                45
        );
    }

    @Test
    void testGetSettings_Success() {
        when(settingsRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(testSettings));
        when(settingsMapper.toSettingsResponse(testSettings)).thenReturn(settingsResponse);

        var response = settingsService.getSettings();

        assertNotNull(response);
        assertTrue(response.strictFormatting());
        verify(settingsRepository).findFirstByOrderByIdAsc();
        verify(settingsMapper).toSettingsResponse(testSettings);
    }

    @Test
    void testGetSettings_NotFound() {
        when(settingsRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> settingsService.getSettings());
        verify(settingsMapper, never()).toSettingsResponse(any(Settings.class));
    }

    @Test
    void testUpdateSettings_ExistingSettings() {
        when(settingsRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(testSettings));
        when(settingsRepository.save(any(Settings.class))).thenReturn(testSettings);
        when(settingsMapper.toSettingsResponse(testSettings)).thenReturn(settingsResponse);

        var response = settingsService.updateSettings(settingsRequest);

        assertNotNull(response);
        verify(settingsRepository).findFirstByOrderByIdAsc();
        verify(settingsRepository).save(testSettings);
        assertEquals(false, testSettings.getStrictFormatting());
        assertEquals(true, testSettings.getAutoCorrect());
        assertEquals(90, testSettings.getValidationThreshold());
        assertEquals(150, testSettings.getRateLimit());
        assertEquals(45, testSettings.getWebhookTimeout());
    }

    @Test
    void testUpdateSettings_CreateNewSettings() {
        when(settingsRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.empty());
        when(settingsRepository.save(any(Settings.class))).thenReturn(testSettings);
        when(settingsMapper.toSettingsResponse(testSettings)).thenReturn(settingsResponse);

        var response = settingsService.updateSettings(settingsRequest);

        assertNotNull(response);
        verify(settingsRepository).findFirstByOrderByIdAsc();
        verify(settingsRepository, times(2)).save(any(Settings.class));
    }

    @Test
    void testUpdateSettings_PartialUpdate() {
        SettingsRequest partialRequest = new SettingsRequest(
                null,
                true,
                null,
                null,
                null
        );

        when(settingsRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(testSettings));
        when(settingsRepository.save(any(Settings.class))).thenReturn(testSettings);
        when(settingsMapper.toSettingsResponse(testSettings)).thenReturn(settingsResponse);

        var response = settingsService.updateSettings(partialRequest);

        assertNotNull(response);
        verify(settingsRepository).save(testSettings);
        assertEquals(true, testSettings.getStrictFormatting()); // unchanged
        assertEquals(true, testSettings.getAutoCorrect()); // updated
        assertEquals(80, testSettings.getValidationThreshold()); // unchanged
    }

    @Test
    void testUpdateSettings_AllNullValues() {
        SettingsRequest nullRequest = new SettingsRequest(
                null,
                null,
                null,
                null,
                null
        );

        when(settingsRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(testSettings));
        when(settingsRepository.save(any(Settings.class))).thenReturn(testSettings);
        when(settingsMapper.toSettingsResponse(testSettings)).thenReturn(settingsResponse);

        var response = settingsService.updateSettings(nullRequest);

        assertNotNull(response);
        verify(settingsRepository).save(testSettings);
        assertEquals(true, testSettings.getStrictFormatting()); // unchanged
        assertEquals(false, testSettings.getAutoCorrect()); // unchanged
        assertEquals(80, testSettings.getValidationThreshold()); // unchanged
    }
}
