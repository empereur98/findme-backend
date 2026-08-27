package com.dhi.findme_backend.service.impl;

import com.dhi.findme_backend.dto.NotificationCreateRequest;
import com.dhi.findme_backend.dto.NotificationResponse;
import com.dhi.findme_backend.entity.Notification;
import com.dhi.findme_backend.entity.User;
import com.dhi.findme_backend.exception.ResourceNotFoundException;
import com.dhi.findme_backend.mapper.NotificationMapper;
import com.dhi.findme_backend.repository.NotificationRepository;
import com.dhi.findme_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private User user;
    private Notification notification;
    private NotificationResponse response;
    private UUID userId;
    private UUID notificationId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        notificationId = UUID.randomUUID();

        user = new User();
        user.setId(userId);
        user.setEmail("user@example.com");

        notification = new Notification();
        notification.setId(notificationId);
        notification.setTitle("Titre");
        notification.setMessage("Message");
        notification.setType("INFO");
        notification.setRead(false);
        notification.setUser(user);

        response = new NotificationResponse(notificationId, "Titre", "Message", "INFO", true, null,
                LocalDateTime.now());
    }

    @Test
    void getNotifications_whenUnreadOnly_filtersOnReadFlag() {
        Pageable pageable = PageRequest.of(0, 10);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(notificationRepository.findByUserAndRead(user, false, pageable))
                .thenReturn(new PageImpl<>(List.of(notification)));
        when(notificationMapper.toNotificationResponse(notification)).thenReturn(response);

        var page = notificationService.getNotifications(userId, pageable, true);

        assertEquals(1, page.getTotalElements());
        verify(notificationRepository).findByUserAndRead(user, false, pageable);
        verify(notificationRepository, never()).findByUser(any(), any());
    }

    @Test
    void getNotifications_whenNotUnreadOnly_returnsAll() {
        Pageable pageable = PageRequest.of(0, 10);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(notificationRepository.findByUser(user, pageable))
                .thenReturn(new PageImpl<>(List.of(notification)));
        when(notificationMapper.toNotificationResponse(notification)).thenReturn(response);

        var page = notificationService.getNotifications(userId, pageable, null);

        assertEquals(1, page.getTotalElements());
        verify(notificationRepository).findByUser(user, pageable);
        verify(notificationRepository, never()).findByUserAndRead(any(), any(), any());
    }

    @Test
    void getNotifications_whenUserMissing_throws() {
        Pageable pageable = PageRequest.of(0, 10);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> notificationService.getNotifications(userId, pageable, false));
    }

    @Test
    void markAsRead_setsReadFlagAndSaves() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(notificationRepository.findByIdAndUser(notificationId, user)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(notification)).thenReturn(notification);
        when(notificationMapper.toNotificationResponse(notification)).thenReturn(response);

        var result = notificationService.markAsRead(notificationId, userId);

        assertTrue(notification.getRead());
        assertEquals(response, result);
        verify(notificationRepository).save(notification);
    }

    @Test
    void markAsRead_whenNotificationDoesNotBelongToUser_throws() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(notificationRepository.findByIdAndUser(notificationId, user)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> notificationService.markAsRead(notificationId, userId));
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void createNotification_persistsUnreadNotificationForUser() {
        NotificationCreateRequest request =
                new NotificationCreateRequest("Titre", "Message", "INFO", "/adresses/1");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));
        when(notificationMapper.toNotificationResponse(any(Notification.class))).thenReturn(response);

        notificationService.createNotification(request, userId);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        Notification saved = captor.getValue();
        assertEquals("Titre", saved.getTitle());
        assertEquals("Message", saved.getMessage());
        assertEquals("INFO", saved.getType());
        assertEquals("/adresses/1", saved.getActionUrl());
        assertFalse(saved.getRead());
        assertEquals(user, saved.getUser());
    }

    @Test
    void markAllAsRead_marksEveryUnreadNotification() {
        Notification other = new Notification();
        other.setId(UUID.randomUUID());
        other.setRead(false);
        other.setUser(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(notificationRepository.findByUserAndRead(eq(user), eq(false), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(notification, other)));

        notificationService.markAllAsRead(userId);

        assertTrue(notification.getRead());
        assertTrue(other.getRead());
        verify(notificationRepository).saveAll(List.of(notification, other));
    }

    @Test
    void markAllAsRead_whenUserMissing_throws() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> notificationService.markAllAsRead(userId));
        verify(notificationRepository, never()).saveAll(any());
    }
}
