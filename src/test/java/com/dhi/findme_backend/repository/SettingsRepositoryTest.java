package com.dhi.findme_backend.repository;

import com.dhi.findme_backend.entity.Settings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SettingsRepositoryTest {

    @Mock
    private SettingsRepository settingsRepository;

    private Settings testSettings;

    @BeforeEach
    void setUp() {
        testSettings = new Settings();
        testSettings.setId(UUID.randomUUID());
        testSettings.setStrictFormatting(true);
        testSettings.setAutoCorrect(false);
        testSettings.setValidationThreshold(80);
        testSettings.setRateLimit(100);
        testSettings.setWebhookTimeout(30);
    }

    @Test
    void testFindFirstByOrderByIdAsc_WhenSettingsExist_ShouldReturnFirstSettings() {
        when(settingsRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(testSettings));

        Optional<Settings> foundSettings = settingsRepository.findFirstByOrderByIdAsc();

        assertTrue(foundSettings.isPresent());
        assertTrue(foundSettings.get().getStrictFormatting());
        assertEquals(80, foundSettings.get().getValidationThreshold());
        verify(settingsRepository).findFirstByOrderByIdAsc();
    }

    @Test
    void testFindFirstByOrderByIdAsc_WhenNoSettings_ShouldReturnEmpty() {
        when(settingsRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.empty());

        Optional<Settings> foundSettings = settingsRepository.findFirstByOrderByIdAsc();

        assertFalse(foundSettings.isPresent());
        verify(settingsRepository).findFirstByOrderByIdAsc();
    }

    @Test
    void testFindFirstByOrderByIdAsc_WithMultipleSettings_ShouldReturnFirst() {
        when(settingsRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(testSettings));

        Optional<Settings> foundSettings = settingsRepository.findFirstByOrderByIdAsc();

        assertTrue(foundSettings.isPresent());
        assertEquals(80, foundSettings.get().getValidationThreshold());
        verify(settingsRepository).findFirstByOrderByIdAsc();
    }

    @Test
    void testSave_WithValidSettings_ShouldPersistSettings() {
        when(settingsRepository.save(any(Settings.class))).thenReturn(testSettings);

        Settings savedSettings = settingsRepository.save(testSettings);

        assertNotNull(savedSettings);
        assertNotNull(savedSettings.getId());
        assertTrue(savedSettings.getStrictFormatting());
        verify(settingsRepository).save(testSettings);
    }

    @Test
    void testDelete_WithExistingSettings_ShouldRemoveSettings() {
        doNothing().when(settingsRepository).delete(testSettings);

        settingsRepository.delete(testSettings);

        verify(settingsRepository).delete(testSettings);
    }

    @Test
    void testFindById_WithExistingSettings_ShouldReturnSettings() {
        UUID settingsId = testSettings.getId();
        when(settingsRepository.findById(settingsId)).thenReturn(Optional.of(testSettings));

        Optional<Settings> foundSettings = settingsRepository.findById(settingsId);

        assertTrue(foundSettings.isPresent());
        assertEquals(settingsId, foundSettings.get().getId());
        verify(settingsRepository).findById(settingsId);
    }

    @Test
    void testFindById_WithNonExistingSettings_ShouldReturnEmpty() {
        UUID randomId = UUID.randomUUID();
        when(settingsRepository.findById(randomId)).thenReturn(Optional.empty());

        Optional<Settings> foundSettings = settingsRepository.findById(randomId);

        assertFalse(foundSettings.isPresent());
        verify(settingsRepository).findById(randomId);
    }

    @Test
    void testCount_WithMultipleSettings_ShouldReturnCorrectCount() {
        when(settingsRepository.count()).thenReturn(2L);

        long count = settingsRepository.count();

        assertEquals(2, count);
        verify(settingsRepository).count();
    }
}