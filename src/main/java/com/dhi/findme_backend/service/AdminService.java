package com.dhi.findme_backend.service;

import com.dhi.findme_backend.dto.AdminStatsResponse;

public interface AdminService {
    AdminStatsResponse getStats();
}