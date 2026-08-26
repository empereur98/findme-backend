package com.dhi.findme_backend.service;

import com.dhi.findme_backend.dto.NotificationCreateRequest;
import com.dhi.findme_backend.dto.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface NotificationService {

    Page<NotificationResponse> getNotifications(UUID userId, Pageable pageable, Boolean unreadOnly);

    NotificationResponse markAsRead(UUID notificationId, UUID userId);

    NotificationResponse createNotification(NotificationCreateRequest request, UUID userId);

    void markAllAsRead(UUID userId);
}
