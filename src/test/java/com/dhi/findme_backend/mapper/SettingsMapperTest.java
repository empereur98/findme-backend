package com.dhi.findme_backend.mapper;

import com.dhi.findme_backend.dto.SettingsRequest;
import com.dhi.findme_backend.dto.SettingsResponse;
import com.dhi.findme_backend.entity.Settings;
import org.mapstruct.factory.Mappers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SettingsMapperTest {

    private SettingsMapper settingsMapper;

    private Settings testSettings;
    private SettingsRequest testRequest;

    @BeforeEach
    void setUp() {
        settingsMapper = Mappers.getMapper(SettingsMapper.class);
        testSettings = new Settings();
        testSettings.setId(UUID.randomUUID());
        testSettings.setStrictFormatting(true);
        testSettings.setAutoCorrect(false);
        testSettings.setValidationThreshold(80);

        testRequest = new SettingsRequest(true, false, 80, null, null);
    }

    @Test
    void testToSettingsResponse_WithAllFields_ShouldMapCorrectly() {
        SettingsResponse response = settingsMapper.toSettingsResponse(testSettings);

        assertNotNull(response);
        assertEquals(testSettings.getStrictFormatting(), response.strictFormatting());
        assertEquals(testSettings.getAutoCorrect(), response.autoCorrect());
        assertEquals(testSettings.getValidationThreshold(), response.validationThreshold());
    }

    @Test
    void testToSettingsResponse_WithNullOptionalFields_ShouldMapCorrectly() {
        testSettings.setStrictFormatting(null);
        testSettings.setAutoCorrect(null);
        testSettings.setValidationThreshold(null);

        SettingsResponse response = settingsMapper.toSettingsResponse(testSettings);

        assertNotNull(response);
        assertNull(response.strictFormatting());
        assertNull(response.autoCorrect());
        assertNull(response.validationThreshold());
    }

    @Test
    void testToSettingsResponse_WithNullEntity_ShouldReturnNull() {
        SettingsResponse response = settingsMapper.toSettingsResponse(null);

        assertNull(response);
    }

    @Test
    void testToSettingsEntity_WithAllFields_ShouldMapCorrectly() {
        Settings entity = settingsMapper.toSettingsEntity(testRequest);

        assertNotNull(entity);
        assertEquals(testRequest.strictFormatting(), entity.getStrictFormatting());
        assertEquals(testRequest.autoCorrect(), entity.getAutoCorrect());
        assertEquals(testRequest.validationThreshold(), entity.getValidationThreshold());
    }

    @Test
    void testToSettingsEntity_WithNullOptionalFields_ShouldMapCorrectly() {
        testRequest = new SettingsRequest(null, null, null, null, null);

        Settings entity = settingsMapper.toSettingsEntity(testRequest);

        assertNotNull(entity);
        assertNull(entity.getStrictFormatting());
        assertNull(entity.getAutoCorrect());
        assertNull(entity.getValidationThreshold());
    }

    @Test
    void testToSettingsEntity_WithNullRequest_ShouldReturnNull() {
        Settings entity = settingsMapper.toSettingsEntity(null);

        assertNull(entity);
    }

    @Test
    void testToSettingsEntity_WithZeroThreshold_ShouldMapCorrectly() {
        testRequest = new SettingsRequest(true, false, 0, null, null);

        Settings entity = settingsMapper.toSettingsEntity(testRequest);

        assertNotNull(entity);
        assertEquals(0, entity.getValidationThreshold());
    }
}
