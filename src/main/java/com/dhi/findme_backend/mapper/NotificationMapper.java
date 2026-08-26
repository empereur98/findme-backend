package com.dhi.findme_backend.mapper;

import com.dhi.findme_backend.dto.NotificationResponse;
import com.dhi.findme_backend.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponse toNotificationResponse(Notification notification) {
        return new NotificationResponse(
            notification.getId(),
            notification.getTitle(),
            notification.getMessage(),
            notification.getType(),
            notification.getRead(),
            notification.getActionUrl(),
            notification.getCreatedAt()
        );
    }
}
