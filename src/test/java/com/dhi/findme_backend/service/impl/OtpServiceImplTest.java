package com.dhi.findme_backend.service.impl;

import com.dhi.findme_backend.dto.OtpRequest;
import com.dhi.findme_backend.dto.OtpVerifyRequest;
import com.dhi.findme_backend.entity.OtpCode;
import com.dhi.findme_backend.entity.OtpType;
import com.dhi.findme_backend.repository.OtpCodeRepository;
import com.dhi.findme_backend.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OtpServiceImplTest {

    @Mock
    private OtpCodeRepository otpCodeRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private OtpServiceImpl otpService;

    @Test
    void generateOtp_persistsSixDigitCodeAndSendsEmail() {
        String code = otpService.generateOtp(new OtpRequest("user@example.com"), OtpType.EMAIL_VERIFICATION);

        assertTrue(code.matches("\\d{6}"), "le code doit contenir 6 chiffres");

        ArgumentCaptor<OtpCode> captor = ArgumentCaptor.forClass(OtpCode.class);
        verify(otpCodeRepository).save(captor.capture());
        OtpCode saved = captor.getValue();
        assertEquals("user@example.com", saved.getEmail());
        assertEquals(code, saved.getCode());
        assertEquals(OtpType.EMAIL_VERIFICATION, saved.getOtpType());
        assertFalse(saved.getUsed());
        assertTrue(saved.getExpiresAt().isAfter(LocalDateTime.now()));
        assertTrue(saved.getExpiresAt().isBefore(LocalDateTime.now().plusMinutes(11)));

        verify(emailService).sendEmail(eq("user@example.com"), contains("vérification"), contains(code));
    }

    @Test
    void generateOtp_forPasswordReset_usesResetSubject() {
        String code = otpService.generateOtp(new OtpRequest("user@example.com"), OtpType.PASSWORD_RESET);

        verify(emailService).sendEmail(eq("user@example.com"), contains("réinitialisation"), contains(code));
    }

    @Test
    void generateOtp_producesDifferentCodes() {
        String first = otpService.generateOtp(new OtpRequest("a@example.com"), OtpType.EMAIL_VERIFICATION);
        String second = otpService.generateOtp(new OtpRequest("a@example.com"), OtpType.EMAIL_VERIFICATION);

        assertTrue(first.matches("\\d{6}"));
        assertTrue(second.matches("\\d{6}"));
        verify(otpCodeRepository, times(2)).save(any(OtpCode.class));
    }

    @Test
    void verifyOtp_withValidCode_marksCodeAsUsed() {
        OtpCode otpCode = new OtpCode("user@example.com", "123456", OtpType.EMAIL_VERIFICATION,
                LocalDateTime.now().plusMinutes(5));
        when(otpCodeRepository.findByEmailAndCodeAndOtpTypeAndUsedFalse(
                "user@example.com", "123456", OtpType.EMAIL_VERIFICATION)).thenReturn(Optional.of(otpCode));

        boolean result = otpService.verifyOtp(
                new OtpVerifyRequest("user@example.com", "123456"), OtpType.EMAIL_VERIFICATION);

        assertTrue(result);
        assertTrue(otpCode.getUsed());
        assertNotNull(otpCode.getUsedAt());
        verify(otpCodeRepository).save(otpCode);
    }

    @Test
    void verifyOtp_withUnknownCode_returnsFalse() {
        when(otpCodeRepository.findByEmailAndCodeAndOtpTypeAndUsedFalse(
                anyString(), anyString(), any(OtpType.class))).thenReturn(Optional.empty());

        boolean result = otpService.verifyOtp(
                new OtpVerifyRequest("user@example.com", "000000"), OtpType.EMAIL_VERIFICATION);

        assertFalse(result);
        verify(otpCodeRepository, never()).save(any());
    }

    @Test
    void verifyOtp_withExpiredCode_returnsFalseAndDoesNotConsumeIt() {
        OtpCode expired = new OtpCode("user@example.com", "123456", OtpType.PASSWORD_RESET,
                LocalDateTime.now().minusMinutes(1));
        when(otpCodeRepository.findByEmailAndCodeAndOtpTypeAndUsedFalse(
                "user@example.com", "123456", OtpType.PASSWORD_RESET)).thenReturn(Optional.of(expired));

        boolean result = otpService.verifyOtp(
                new OtpVerifyRequest("user@example.com", "123456"), OtpType.PASSWORD_RESET);

        assertFalse(result);
        assertFalse(expired.getUsed());
        verify(otpCodeRepository, never()).save(any());
    }

    @Test
    void cleanupExpiredOtps_deletesCodesExpiredBeforeNow() {
        otpService.cleanupExpiredOtps();

        ArgumentCaptor<LocalDateTime> captor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(otpCodeRepository).deleteByExpiresAtBefore(captor.capture());
        assertFalse(captor.getValue().isAfter(LocalDateTime.now()));
    }
}
