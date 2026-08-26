package com.dhi.findme_backend.service.impl;

import com.dhi.findme_backend.dto.CampayWebhookEvent;
import com.dhi.findme_backend.dto.NotificationCreateRequest;
import com.dhi.findme_backend.entity.User;
import com.dhi.findme_backend.repository.UserRepository;
import com.dhi.findme_backend.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");
        user.setPlan("free");
        user.setMaxAddresses(5);
    }

    @Test
    void handleSuccessfulPayment_shouldUpgradeUserToPro() {
        // Arrange
        CampayWebhookEvent webhookEvent = new CampayWebhookEvent(
            "test-tx-ref",
            null,
            "SUCCESSFUL",
            "5000",
            "XAF",
            "orange",
            null,
            null,
            "+221771234567",
            "test@example.com"
        );
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // Act
        paymentService.handleWebhook(webhookEvent);

        // Assert
        assertEquals("pro", user.getPlan());
        assertEquals(100, user.getMaxAddresses());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void handleFailedPayment_shouldNotChangeUserPlan() {
        // Arrange
        CampayWebhookEvent webhookEvent = new CampayWebhookEvent(
            "test-tx-ref",
            null,
            "FAILED",
            "5000",
            "XAF",
            "orange",
            null,
            null,
            "+221771234567",
            "test@example.com"
        );
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // Act
        paymentService.handleWebhook(webhookEvent);

        // Assert
        assertEquals("free", user.getPlan());
        assertEquals(5, user.getMaxAddresses());
        verify(userRepository, never()).save(user);
    }
}