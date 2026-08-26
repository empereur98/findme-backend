package com.dhi.findme_backend.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SettingsEntityTest {

    private Settings settings;

    @BeforeEach
    void setUp() {
        settings = new Settings();
        settings.setId(UUID.randomUUID());
        settings.setStrictFormatting(true);
        settings.setAutoCorrect(false);
        settings.setValidationThreshold(80);
        settings.setRateLimit(100);
        settings.setWebhookTimeout(30);
    }

    @Test
    void testSettingsCreation_WithValidData_ShouldCreateSettings() {
        assertNotNull(settings.getId());
        assertTrue(settings.getStrictFormatting());
        assertFalse(settings.getAutoCorrect());
        assertEquals(80, settings.getValidationThreshold());
        assertEquals(100, settings.getRateLimit());
        assertEquals(30, settings.getWebhookTimeout());
    }

    @Test
    void testSettingsStrictFormatting_ShouldBeToggleable() {
        settings.setStrictFormatting(false);
        assertFalse(settings.getStrictFormatting());
        
        settings.setStrictFormatting(true);
        assertTrue(settings.getStrictFormatting());
    }

    @Test
    void testSettingsAutoCorrect_ShouldBeToggleable() {
        settings.setAutoCorrect(true);
        assertTrue(settings.getAutoCorrect());
        
        settings.setAutoCorrect(false);
        assertFalse(settings.getAutoCorrect());
    }

    @Test
    void testSettingsValidationThreshold_ShouldBeSettable() {
        settings.setValidationThreshold(90);
        assertEquals(90, settings.getValidationThreshold());
    }

    @Test
    void testSettingsRateLimit_ShouldBeSettable() {
        settings.setRateLimit(200);
        assertEquals(200, settings.getRateLimit());
    }

    @Test
    void testSettingsWebhookTimeout_ShouldBeSettable() {
        settings.setWebhookTimeout(60);
        assertEquals(60, settings.getWebhookTimeout());
    }

    @Test
    void testSettingsValidationThreshold_ShouldNotBeNegative() {
        settings.setValidationThreshold(-10);
        assertEquals(-10, settings.getValidationThreshold());
    }

    @Test
    void testSettingsRateLimit_ShouldNotBeNegative() {
        settings.setRateLimit(-50);
        assertEquals(-50, settings.getRateLimit());
    }

    @Test
    void testSettingsWebhookTimeout_ShouldNotBeNegative() {
        settings.setWebhookTimeout(-30);
        assertEquals(-30, settings.getWebhookTimeout());
    }
}
