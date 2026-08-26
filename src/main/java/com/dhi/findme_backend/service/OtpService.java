package com.dhi.findme_backend.service;

import com.dhi.findme_backend.dto.OtpRequest;
import com.dhi.findme_backend.dto.OtpVerifyRequest;
import com.dhi.findme_backend.entity.OtpCode;
import com.dhi.findme_backend.entity.OtpType;
import com.dhi.findme_backend.repository.OtpCodeRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

public interface OtpService {
    
    String generateOtp(OtpRequest request, OtpType otpType);
    
    boolean verifyOtp(OtpVerifyRequest request, OtpType otpType);
    
    void cleanupExpiredOtps();
}
