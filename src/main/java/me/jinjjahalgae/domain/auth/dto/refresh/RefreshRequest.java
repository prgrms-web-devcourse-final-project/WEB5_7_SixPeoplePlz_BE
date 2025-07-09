package me.jinjjahalgae.domain.auth.dto.refresh;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 리프레시 요청 dto
 */
@Schema(
    title = "리프레시 요청",
    description = "리프레시 토큰으로 access/refresh 토큰을 재발급할 때 사용하는 요청 DTO"
)
public record RefreshRequest(
    @Schema(
        description = "기존 리프레시 토큰",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "eyJhbGc123451NiJ9.eyJzdWIiOiI2IiwiaWF0IjoxNzUxODMyNjMxLCJleHAiOjE3NTE4MzQ0MzF9.hY7PLaNrQifTgHUjg8Jb2899FQfCSoiGGJv6-yl6jS0"
    )
    @NotBlank(message = "refreshToken은 필수입니다.")
    String refreshToken
) {} 