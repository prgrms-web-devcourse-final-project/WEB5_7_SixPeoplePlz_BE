package me.jinjjahalgae.domain.notification.usecase.listener.event;

import java.util.List;

/**
 * FCM 푸시 알림 전송이 필요할 때 발행하는 이벤트
 *
 * @param targetUserIds 푸시 알림 수신할 사용자 ID 목록
 * @param title         푸시 알림 제목
 * @param body          푸시 알림 내용
 */
public record PushNotificationSendEvent(
        List<Long> targetUserIds,
        String title,
        String body
) {
}
