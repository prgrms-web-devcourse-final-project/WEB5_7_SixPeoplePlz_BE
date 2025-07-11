package me.jinjjahalgae.domain.notification.usecase.listener.event;

import me.jinjjahalgae.domain.notification.enums.NotificationType;

/**
 * 알림 생성이 필요할 때 발행하는 이벤트
 *
 * @param notificationType  발행할 알림의 종류
 * @param contractId        알림과 관련된 계약 ID
 * @param actorUserId       알림을 발생시킨 행동 주체인 사용자 ID
 */
public record NotificationEvent(
        NotificationType notificationType,
        Long contractId,
        Long actorUserId
) {}