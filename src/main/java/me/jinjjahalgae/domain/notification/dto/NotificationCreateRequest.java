package me.jinjjahalgae.domain.notification.dto;

import jakarta.validation.constraints.NotNull;
import me.jinjjahalgae.domain.notification.enums.NotificationType;

/**
 * 알림 생성 요청 dto
 * @param type NotificationType enum
 * @param contractId 관련 계약ID를 각 도메인에서 넘겨줌
 * @param actorUserId 인증 추가, 피드백 추가, 승인 거절 등 행동의 주체인 유저ID
 */
public record NotificationCreateRequest(
        @NotNull NotificationType type,
        @NotNull Long contractId,
        @NotNull Long actorUserId
) {
}
