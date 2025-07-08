package me.jinjjahalgae.domain.notification.dto;

import me.jinjjahalgae.domain.notification.entities.Notification;
import me.jinjjahalgae.domain.notification.enums.NotificationType;

/**
 * userID로 알림 목록 조회했을 때 각 알림정보를 담은 DTO.
 * 알림 전체 목록 조회하는 api에서 해당 DTO의 list를 반환할 예정
 * @param notificationId 알림의 id
 * @param type 알림의 enum 타입
 * @param readStatus 알림의 read 상태 true/false
 * @param content 알림 본문
 * @param targetUserId 알림을 받을 유저 ID 정보 (FK)
 * @param contractId 클릭했을 때 이동할 계약 ID 정보 (FK)
 */
public record NotificationGetResponse(
        Long notificationId,
        NotificationType type,
        boolean readStatus,
        String content,
        Long targetUserId,
        Long contractId
) {
    /// Notification -> NotificationGetResponse 변환 메서드
    public static NotificationGetResponse from(Notification notification) {
        return new NotificationGetResponse(
                notification.getId(),
                notification.getType(),
                notification.isRead(),
                notification.getContent(),
                notification.getUserId(),
                notification.getContractId()
        );
    }
}
