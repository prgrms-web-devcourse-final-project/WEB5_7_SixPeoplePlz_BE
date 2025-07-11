package me.jinjjahalgae.domain.feedback.usecase.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import me.jinjjahalgae.domain.feedback.enums.FeedbackStatus;

import java.time.LocalDateTime;

/**
 * 인증 상세 내 피드백 response
 */
@Schema(
        title = "인증 상세 페이지 피드백 조회",
        description = "상세 페이지 내 피드백 응답 DTO",
        example = """
        {
          "createdAt": "2025-07-09T13:30:00",
          "status": "APPROVED",
          "comment": "확인했습니다!"
        }
        """
)
public record FeedbackResponse(
        @Schema(description = "피드백 생성일")
        LocalDateTime createdAt,

        @Schema(description = "피드백 상태 (APPROVED:승인, REJECTED: 거절")
        FeedbackStatus status,

        @Schema(description = "코멘트 (없으면 null)")
        String comment

        // 인증 사진들?
) {
}
