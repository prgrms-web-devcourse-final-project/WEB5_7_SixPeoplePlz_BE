package me.jinjjahalgae.domain.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    title = "알림 생성 응답",
    description = "알림 생성 완료 후 반환되는 응답 DTO"
)
public record NotificationCreateResponse(
    @Schema(description = "생성된 알림 개수",
        example = "3",
        requiredMode = Schema.RequiredMode.REQUIRED)
    int notificationCnt,
    
    @Schema(description = "생성된 알림 메시지 (확인용)",
        example = "'홍길동'님이 '매일 운동하기' 계약의 감독자로 추가되었습니다.",
        requiredMode = Schema.RequiredMode.REQUIRED)
    String message
) {
}
