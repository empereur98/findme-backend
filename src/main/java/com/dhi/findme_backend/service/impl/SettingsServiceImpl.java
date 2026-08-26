package com.dhi.findme_backend.service.impl;

import com.dhi.findme_backend.dto.SettingsRequest;
import com.dhi.findme_backend.dto.SettingsResponse;
import com.dhi.findme_backend.entity.Settings;
import com.dhi.findme_backend.exception.ResourceNotFoundException;
import com.dhi.findme_backend.mapper.SettingsMapper;
import com.dhi.findme_backend.repository.SettingsRepository;
import com.dhi.findme_backend.service.SettingsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SettingsServiceImpl implements SettingsService {

    private final SettingsRepository settingsRepository;
    private final SettingsMapper settingsMapper;

    public SettingsServiceImpl(SettingsRepository settingsRepository, SettingsMapper settingsMapper) {
        this.settingsRepository = settingsRepository;
        this.settingsMapper = settingsMapper;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public SettingsResponse getSettings() {
        Settings settings = settingsRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new ResourceNotFoundException("SETTINGS_NOT_FOUND", "Paramètres introuvables"));
        return settingsMapper.toSettingsResponse(settings);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public SettingsResponse updateSettings(SettingsRequest request) {
        Settings settings = settingsRepository.findFirstByOrderByIdAsc()
                .orElseGet(() -> {
                    Settings newSettings = new Settings();
                    return settingsRepository.save(newSettings);
                });

        if (request.strictFormatting() != null) {
            settings.setStrictFormatting(request.strictFormatting());
        }
        if (request.autoCorrect() != null) {
            settings.setAutoCorrect(request.autoCorrect());
        }
        if (request.validationThreshold() != null) {
            settings.setValidationThreshold(request.validationThreshold());
        }
        if (request.rateLimit() != null) {
            settings.setRateLimit(request.rateLimit());
        }
        if (request.webhookTimeout() != null) {
            settings.setWebhookTimeout(request.webhookTimeout());
        }

        settings = settingsRepository.save(settings);
        return settingsMapper.toSettingsResponse(settings);
    }
}
