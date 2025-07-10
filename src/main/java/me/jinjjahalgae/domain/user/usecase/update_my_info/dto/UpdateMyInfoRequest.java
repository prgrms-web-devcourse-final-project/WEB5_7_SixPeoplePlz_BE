package me.jinjjahalgae.domain.user.usecase.update_my_info.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 내 정보 수정 요청 dto
 */
@Schema(
        title = "내 유저정보 수정 요청",
        description = "내 유저정보 수정 DTO"
)
public record UpdateMyInfoRequest(
    @Schema(description = "닉네임", example = "홍길동")
    @NotBlank(message = "nickname은 필수입니다.")
    String nickname
) {} 