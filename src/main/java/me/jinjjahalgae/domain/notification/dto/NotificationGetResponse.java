package me.jinjjahalgae.domain.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import me.jinjjahalgae.domain.notification.entities.Notification;
import me.jinjjahalgae.domain.notification.enums.NotificationType;

@Schema(
    title = "알림 조회 응답",
    description = "사용자 ID로 조회한 알림 목록의 각 알림 정보를 담은 DTO"
)
public record NotificationGetResponse(
    @Schema(description = "알림 ID",
        example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED)
    Long notificationId,
    
    @Schema(description = "알림 타입",
        example = "CONTRACT_STARTED",
        allowableValues = {"SUPERVISOR_ADDED", "SUPERVISOR_WITHDRAWN", "CONTRACT_STARTED", 
                          "CONTRACT_ENDED_FAIL", "CONTRACT_ENDED_SUCCESS", "PROOF_ADDED", 
                          "PROOF_ACCEPTED", "PROOF_REJECTED", "FEEDBACK_ADDED", "REPROOF_ADDED"},
        requiredMode = Schema.RequiredMode.REQUIRED)
    NotificationType type,
    
    @Schema(description = "알림 읽음 상태",
        example = "false",
        requiredMode = Schema.RequiredMode.REQUIRED)
    boolean readStatus,
    
    @Schema(description = "알림 본문 내용",
        example = "'홍길동'님의 '매일 운동하기' 계약이 시작되었습니다.",
        requiredMode = Schema.RequiredMode.REQUIRED)
    String content,
    
    @Schema(description = "알림을 받을 사용자 ID",
        example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED)
    Long targetUserId,
    
    @Schema(description = "관련 계약 ID (클릭 시 이동할 계약)",
        example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED)
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
