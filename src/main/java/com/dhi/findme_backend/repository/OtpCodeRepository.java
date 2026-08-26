package com.dhi.findme_backend.repository;

import com.dhi.findme_backend.entity.OtpCode;
import com.dhi.findme_backend.entity.OtpType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OtpCodeRepository extends JpaRepository<OtpCode, UUID> {
    
    Optional<OtpCode> findByEmailAndCodeAndOtpTypeAndUsedFalse(String email, String code, OtpType otpType);
    
    @Query("SELECT o FROM OtpCode o WHERE o.email = :email AND o.otpType = :otpType AND o.used = false ORDER BY o.createdAt DESC")
    Optional<OtpCode> findLatestUnusedByType(String email, OtpType otpType);
    
    void deleteByExpiresAtBefore(LocalDateTime date);
}
