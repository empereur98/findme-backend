package com.dhi.findme_backend.service;

import com.dhi.findme_backend.dto.PaymentResponse;
import com.dhi.findme_backend.dto.SubscriptionResponse;
import com.dhi.findme_backend.dto.SubscriptionUpgradeRequest;
import com.dhi.findme_backend.entity.User;
import com.dhi.findme_backend.exception.ResourceNotFoundException;
import com.dhi.findme_backend.repository.UserRepository;
import com.dhi.findme_backend.service.PaymentService;
import com.dhi.findme_backend.service.impl.SubscriptionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    private User testUser;
    private User proUser;
    private SubscriptionUpgradeRequest upgradeRequest;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        testUser = new User();
        testUser.setId(userId);
        testUser.setEmail("john.doe@example.com");
        testUser.setPlan("free");
        testUser.setMaxAddresses(4);
        testUser.setAddressesCreatedCount(2);

        proUser = new User();
        proUser.setId(userId);
        proUser.setEmail("john.doe@example.com");
        proUser.setPlan("pro");
        proUser.setMaxAddresses(100);
        proUser.setAddressesCreatedCount(5);

        upgradeRequest = new SubscriptionUpgradeRequest(
                "orange_money",
                "771234567",
                "+221",
                "pro"
        );
    }

    @Test
    void testUpgradeSubscription_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        PaymentResponse paymentResponse = new PaymentResponse(true, "Success", "http://payment.link", "123", "pending", null, null, null);
        when(paymentService.initiatePayment(any())).thenReturn(paymentResponse);

        var response = subscriptionService.upgradeSubscription(upgradeRequest, userId);

        assertNotNull(response);
        verify(userRepository).findById(userId);
        verify(paymentService).initiatePayment(any());
    }

    @Test
    void testUpgradeSubscription_UserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> subscriptionService.upgradeSubscription(upgradeRequest, userId));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpgradeSubscription_AlreadyPro() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(proUser));
        PaymentResponse paymentResponse = new PaymentResponse(true, "Success", "http://payment.link", "123", "pending", null, null, null);
        when(paymentService.initiatePayment(any())).thenReturn(paymentResponse);

        var response = subscriptionService.upgradeSubscription(upgradeRequest, userId);

        assertNotNull(response);
        verify(userRepository).findById(userId);
        verify(paymentService).initiatePayment(any());
    }

    @Test
    void testGetSubscriptionStatus_FreeUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        var response = subscriptionService.getSubscriptionStatus(userId);

        assertNotNull(response);
        verify(userRepository).findById(userId);
    }

    @Test
    void testGetSubscriptionStatus_ProUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(proUser));

        var response = subscriptionService.getSubscriptionStatus(userId);

        assertNotNull(response);
        verify(userRepository).findById(userId);
    }

    @Test
    void testGetSubscriptionStatus_UserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> subscriptionService.getSubscriptionStatus(userId));
    }

    @Test
    void testCancelSubscription_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(proUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        subscriptionService.cancelSubscription(userId);

        verify(userRepository).findById(userId);
        verify(userRepository).save(proUser);
        assertEquals("free", proUser.getPlan());
        assertEquals(4, proUser.getMaxAddresses());
    }

    @Test
    void testCancelSubscription_UserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> subscriptionService.cancelSubscription(userId));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testCancelSubscription_AlreadyFree() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        subscriptionService.cancelSubscription(userId);

        verify(userRepository).save(testUser);
        assertEquals("free", testUser.getPlan());
        assertEquals(4, testUser.getMaxAddresses());
    }
}
