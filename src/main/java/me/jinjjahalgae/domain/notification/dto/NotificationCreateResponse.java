package me.jinjjahalgae.domain.notification.dto;


public record NotificationCreateResponse(
        Long notificationId,
        String message
) {
}
