package me.jinjjahalgae.domain.notification.dto;

import me.jinjjahalgae.domain.notification.enums.NotificationType;

/**
 * userID로 알림 목록 조회했을 때 각 알림정보를 담은 DTO
 * @param notificationId
 * @param type
 * @param readStatus
 * @param content
 * @param targetUserId
 * @param contractId
 */
public record NotificationResponse(
        Long notificationId,
        NotificationType type,
        boolean readStatus,
        String content, // 알림 본문
        Long targetUserId, // 알림을 받을 유저 ID 정보 (FK)
        Long contractId // 클릭했을 때 이동할 계약 ID 정보 (FK)
) {
}
