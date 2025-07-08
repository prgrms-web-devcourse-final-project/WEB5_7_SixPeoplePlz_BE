package me.jinjjahalgae.domain.notification.dto;

import jakarta.validation.constraints.NotNull;
import me.jinjjahalgae.domain.notification.enums.NotificationType;

/**
 * 알림 생성 요청 dto (백엔드에서만 쓰일 예정)
 * @param type NotificationType enum
 * @param contractId 관련 계약ID를 각 도메인에서 넘겨받음
 * @param actorUserId 인증 추가, 피드백 추가, 승인 거절 등의 행동 주체인 유저ID.
 *                    이를 통해 "ㅇㅇㅇ님이 계약을 종료했습니다" 등의 메세지를 생성할 예정
 */
public record NotificationCreateRequest(
        @NotNull NotificationType type,
        @NotNull Long contractId,
        @NotNull Long actorUserId
) {
}
