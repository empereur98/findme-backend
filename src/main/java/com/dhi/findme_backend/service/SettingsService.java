package com.dhi.findme_backend.service;

import com.dhi.findme_backend.dto.SettingsRequest;
import com.dhi.findme_backend.dto.SettingsResponse;

public interface SettingsService {

    SettingsResponse getSettings();

    SettingsResponse updateSettings(SettingsRequest request);
}
