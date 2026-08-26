package com.dhi.findme_backend.service;

import com.dhi.findme_backend.dto.ExportResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ExportService {

    ExportResponse generatePdfExport(UUID addressId, UUID userId);

    Page<ExportResponse> getExportHistory(Pageable pageable, UUID userId);

    void cancelSubscription(UUID userId);
}
