package me.jinjjahalgae.domain.notification.usecase.listener.event;

import me.jinjjahalgae.domain.notification.enums.NotificationType;
import me.jinjjahalgae.domain.notification.model.NotificationData;

import java.util.List;

public record NotificationBatchEvent(
        NotificationType notificationType,
        List<NotificationData> notificationData
) {
}
