package com.dhi.findme_backend.service.impl;

import com.dhi.findme_backend.dto.NotificationCreateRequest;
import com.dhi.findme_backend.dto.NotificationResponse;
import com.dhi.findme_backend.entity.Notification;
import com.dhi.findme_backend.entity.User;
import com.dhi.findme_backend.exception.ResourceNotFoundException;
import com.dhi.findme_backend.mapper.NotificationMapper;
import com.dhi.findme_backend.repository.NotificationRepository;
import com.dhi.findme_backend.repository.UserRepository;
import com.dhi.findme_backend.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   UserRepository userRepository,
                                   NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.notificationMapper = notificationMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getNotifications(UUID userId, Pageable pageable, Boolean unreadOnly) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("USER_NOT_FOUND", "Utilisateur non trouvé"));

        Page<Notification> notifications;
        if (Boolean.TRUE.equals(unreadOnly)) {
            notifications = notificationRepository.findByUserAndRead(user, false, pageable);
        } else {
            notifications = notificationRepository.findByUser(user, pageable);
        }

        return notifications.map(notificationMapper::toNotificationResponse);
    }

    @Override
    public NotificationResponse markAsRead(UUID notificationId, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("USER_NOT_FOUND", "Utilisateur non trouvé"));

        Notification notification = notificationRepository.findByIdAndUser(notificationId, user)
                .orElseThrow(() -> new ResourceNotFoundException("NOTIFICATION_NOT_FOUND", "Notification introuvable"));

        notification.setRead(true);
        notification = notificationRepository.save(notification);

        return notificationMapper.toNotificationResponse(notification);
    }

    @Override
    public NotificationResponse createNotification(NotificationCreateRequest request, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("USER_NOT_FOUND", "Utilisateur non trouvé"));

        Notification notification = new Notification();
        notification.setTitle(request.title());
        notification.setMessage(request.message());
        notification.setType(request.type());
        notification.setActionUrl(request.actionUrl());
        notification.setRead(false);
        notification.setUser(user);

        notification = notificationRepository.save(notification);

        return notificationMapper.toNotificationResponse(notification);
    }

    @Override
    public void markAllAsRead(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("USER_NOT_FOUND", "Utilisateur non trouvé"));

        Page<Notification> unreadNotifications = notificationRepository.findByUserAndRead(user, false, Pageable.unpaged());
        unreadNotifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(unreadNotifications.getContent());
    }
}