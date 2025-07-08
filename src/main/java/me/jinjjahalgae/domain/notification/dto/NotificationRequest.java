package me.jinjjahalgae.domain.notification.dto;

/**
 * 특정 userID의 알림 목록을 조회할 때의 request DTO
 */
public record NotificationRequest(
        Long userId
) {
}
