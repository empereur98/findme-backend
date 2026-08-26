package com.dhi.findme_backend.service.impl;

import com.dhi.findme_backend.dto.OtpRequest;
import com.dhi.findme_backend.dto.OtpVerifyRequest;
import com.dhi.findme_backend.entity.OtpCode;
import com.dhi.findme_backend.entity.OtpType;
import com.dhi.findme_backend.repository.OtpCodeRepository;
import com.dhi.findme_backend.service.EmailService;
import com.dhi.findme_backend.service.OtpService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class OtpServiceImpl implements OtpService {
    
    private final OtpCodeRepository otpCodeRepository;
    private final EmailService emailService;
    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 10;
    private final SecureRandom secureRandom = new SecureRandom();
    
    public OtpServiceImpl(OtpCodeRepository otpCodeRepository, EmailService emailService) {
        this.otpCodeRepository = otpCodeRepository;
        this.emailService = emailService;
    }
    
    @Override
    public String generateOtp(OtpRequest request, OtpType otpType) {
        String code = generateRandomCode();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);
        
        OtpCode otpCode = new OtpCode(request.email(), code, otpType, expiresAt);
        otpCodeRepository.save(otpCode);
        
        String subject = getSubject(otpType);
        String body = getBody(otpType, code);
        emailService.sendEmail(request.email(), subject, body);
        
        return code;
    }
    
    @Override
    public boolean verifyOtp(OtpVerifyRequest request, OtpType otpType) {
        Optional<OtpCode> otpCodeOpt = otpCodeRepository.findByEmailAndCodeAndOtpTypeAndUsedFalse(
            request.email(), request.code(), otpType
        );
        
        if (otpCodeOpt.isEmpty()) {
            return false;
        }
        
        OtpCode otpCode = otpCodeOpt.get();
        
        if (!otpCode.isValid()) {
            return false;
        }
        
        otpCode.setUsed(true);
        otpCode.setUsedAt(LocalDateTime.now());
        otpCodeRepository.save(otpCode);
        
        return true;
    }
    
    @Override
    public void cleanupExpiredOtps() {
        otpCodeRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }
    
    private String generateRandomCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            code.append(secureRandom.nextInt(10));
        }
        return code.toString();
    }
    
    private String getSubject(OtpType otpType) {
        return switch (otpType) {
            case EMAIL_VERIFICATION -> "Code de vérification de votre email";
            case PASSWORD_RESET -> "Code de réinitialisation de votre mot de passe";
        };
    }
    
    private String getBody(OtpType otpType, String code) {
        return switch (otpType) {
            case EMAIL_VERIFICATION -> String.format(
                "Votre code de vérification est : %s\n\n" +
                "Ce code expire dans %d minutes.\n\n" +
                "Si vous n'avez pas demandé cette vérification, ignorez cet email.",
                code, OTP_EXPIRY_MINUTES
            );
            case PASSWORD_RESET -> String.format(
                "Votre code de réinitialisation de mot de passe est : %s\n\n" +
                "Ce code expire dans %d minutes.\n\n" +
                "Si vous n'avez pas demandé cette réinitialisation, ignorez cet email.",
                code, OTP_EXPIRY_MINUTES
            );
        };
    }
}
