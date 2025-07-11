package me.jinjjahalgae.domain.notification.usecase.create.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import me.jinjjahalgae.domain.notification.enums.NotificationType;
import me.jinjjahalgae.global.validation.EnumValue;

/**
 * 알림 생성 요청 dto (백엔드에서만 쓰일 예정)
 * @param type NotificationType enum
 * @param contractId 관련 계약ID를 각 도메인에서 넘겨받음
 * @param actorUserId 인증 추가, 피드백 추가, 승인 거절 등의 행동 주체인 유저ID.
 *                    이를 통해 "ㅇㅇㅇ님이 계약을 종료했습니다" 등의 메세지를 생성할 예정
 */

@Schema(
    title = "알림 생성 요청",
    description = "새로운 알림을 생성하기 위한 요청 DTO (백엔드 내부 사용)"
)
public record NotificationCreateRequest(
    @Schema(description = "알림 타입",
        example = "CONTRACT_STARTED",
        requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "알림 타입은 필수입니다.")
    @EnumValue(enumClass = NotificationType.class, message = "type은 SUPERVISOR_ADDED, SUPERVISOR_WITHDRAWN, CONTRACT_STARTED, CONTRACT_ENDED_FAIL, CONTRACT_ENDED_SUCCESS, PROOF_ADDED, PROOF_ACCEPTED, PROOF_REJECTED, FEEDBACK_ADDED, REPROOF_ADDED 중 하나여야 합니다.")
    NotificationType type,
    
    @Schema(description = "관련 계약 ID",
        example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "계약id는 필수입니다.")
    Long contractId,
    
    @Schema(description = "행동 주체인 사용자 ID (ㅇㅇㅇ님이 ㅇㅇ했습니다 형식의 알림 메시지 생성에 사용)",
        example = "2",
        requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "행동 주체인 user의 id는 필수입니다.")
    Long actorUserId
) {
}
