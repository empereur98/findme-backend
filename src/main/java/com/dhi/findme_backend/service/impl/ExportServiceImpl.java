package com.dhi.findme_backend.service.impl;

import com.dhi.findme_backend.dto.ExportResponse;
import com.dhi.findme_backend.entity.Address;
import com.dhi.findme_backend.entity.Export;
import com.dhi.findme_backend.entity.User;
import com.dhi.findme_backend.exception.BusinessException;
import com.dhi.findme_backend.exception.ResourceNotFoundException;
import com.dhi.findme_backend.mapper.ExportMapper;
import com.dhi.findme_backend.repository.AddressRepository;
import com.dhi.findme_backend.repository.ExportRepository;
import com.dhi.findme_backend.repository.UserRepository;
import com.dhi.findme_backend.service.ExportService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
@Transactional
public class ExportServiceImpl implements ExportService {

    private static final int EXPORT_EXPIRATION_DAYS = 7;
    private static final int MIN_CODE_DIGITS = 100;
    private static final int MAX_CODE_DIGITS = 900;

    private final ExportRepository exportRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final ExportMapper exportMapper;

    public ExportServiceImpl(ExportRepository exportRepository, UserRepository userRepository,
                             AddressRepository addressRepository, ExportMapper exportMapper) {
        this.exportRepository = exportRepository;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.exportMapper = exportMapper;
    }

    @Override
    public ExportResponse generatePdfExport(UUID addressId, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("USER_NOT_FOUND", "Utilisateur non trouvé"));

        if (!"pro".equals(user.getPlan())) {
            throw new BusinessException("PRO_PLAN_REQUIRED", "L'utilisateur est en plan free");
        }

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("ADDRESS_NOT_FOUND", "Adresse introuvable"));

        String code = generateExportCode();
        String filename = "ADREES_" + address.getName().replace(" ", "_") + ".pdf";
        String downloadUrl = "https://cdn.adrees.africa/exports/" + code + ".pdf";
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(EXPORT_EXPIRATION_DAYS);

        Export export = new Export();
        export.setCode(code);
        export.setFilename(filename);
        export.setDownloadUrl(downloadUrl);
        export.setExpiresAt(expiresAt);
        export.setSize("1.2 MB");
        export.setUser(user);
        export.setAddress(address);

        export = exportRepository.save(export);
        return exportMapper.toExportResponse(export);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExportResponse> getExportHistory(Pageable pageable, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("USER_NOT_FOUND", "Utilisateur non trouvé"));
        Page<Export> exports = exportRepository.findByUser(user, pageable);
        return exports.map(exportMapper::toExportResponse);
    }

    @Override
    public void cancelSubscription(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("USER_NOT_FOUND", "Utilisateur non trouvé"));
        user.setPlan("free");
        userRepository.save(user);
    }

    private String generateExportCode() {
        Random random = new Random();
        int digits = MIN_CODE_DIGITS + random.nextInt(MAX_CODE_DIGITS);
        return "EXP-" + digits;
    }
}