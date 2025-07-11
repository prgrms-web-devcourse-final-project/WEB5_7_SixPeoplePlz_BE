package me.jinjjahalgae.domain.feedback.usecase.create.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import me.jinjjahalgae.domain.feedback.enums.FeedbackStatus;
import me.jinjjahalgae.domain.proof.entities.Proof;
import me.jinjjahalgae.global.validation.EnumValue;

/**
 * 인증에 대한 감독자의 피드백 생성 요청 dto
 */
@Schema(
        title = "인증에 대한 감독자의 피드백 생성 요청",
        description = "인증에 대한 감독자의 피드백 생성 요청 DTO"
)
public record CreateFeedbackRequest(
        @Schema(
                description = "댓글",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                example = "솔직히 이거 노인정"
        )
        @NotBlank(message = "comment는 필수입니다.")
        @Size(max = 100, message = "comment는 100자 이하여야 합니다.")
        String comment,

        @Schema(
                description = "피드백 상태 (APPROVED, REJECTED)",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                example = "REJECTED"
        )
        @NotBlank(message = "status는 필수입니다.")
        @EnumValue(enumClass = FeedbackStatus.class, message = "status는 APPROVED, REJECTED 중 하나여야 합니다.")
        String status,

        @Schema(
                description = "피드백을 작성할 인증 id",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                example = "1"
        )
        @NotNull(message = "proofId는 필수입니다.")
        Long proofId
) {
}
