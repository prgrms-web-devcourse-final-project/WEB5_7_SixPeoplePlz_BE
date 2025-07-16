package me.jinjjahalgae.domain.feedback.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "피드백 상태 (APPROVED, REJECTED)",
        example = "REJECTED",
        enumAsRef = true
)
public enum FeedbackStatus {
    APPROVED,
    REJECTED,
}
