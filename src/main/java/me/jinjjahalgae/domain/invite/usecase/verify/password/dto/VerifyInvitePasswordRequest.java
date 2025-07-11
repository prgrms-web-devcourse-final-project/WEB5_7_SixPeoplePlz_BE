package me.jinjjahalgae.domain.invite.usecase.verify.password.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 초대링크 비밀번호 확인 request
 * @param password 비밀번호
 */
@Schema(
        title = "초대시 계약서 미리보기 요청",
        description = "초대시 계약서 미리보기 요청 DTO"
)
public record VerifyInvitePasswordRequest(
        @Schema(
                description = "초대코드에 맞는 비밀번호",
                requiredMode = Schema.RequiredMode.REQUIRED,
                example = "3cd039f4"
        )
        @NotBlank(message = "password는 필수 입력 값입니다.")
        String password
) {
}
