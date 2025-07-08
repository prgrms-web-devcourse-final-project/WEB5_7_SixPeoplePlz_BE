package me.jinjjahalgae.domain.notification.dto;

/**
 * 알림 생성 후 응답 dto
 * @param notificationCnt DB에 추가된 알림의 개수
 * @param message 생성된 메세지 확인용
 */
public record NotificationCreateResponse(
        int notificationCnt,
        String message
) {
}
