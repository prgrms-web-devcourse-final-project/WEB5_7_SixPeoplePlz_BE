package me.jinjjahalgae.domain.auth.usecase.refresh.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 리프레시 응답 dto
 */
@Schema(
    title = "리프레시 응답",
    description = "리프레시 성공 시 JWT 응답 DTO",
    example = """
    {
      "accessToken": "eyJhbGc123451NiJ9.eyJzdWIiOiI2IiwiaWF0IjoxNzUxODMyNjMxLCJleHAiOjE3NTE4MzQ0MzF9.hY7PLaNrQifTgHUjg8Jb2899FQfCSoiGGJv6-yl6jS0",
      "refreshToken": "eyJhbGc123451NiJ9.eyJzdWIiOiI2IiwiaWF0IjoxNzUxODMyNjMxLCJleHAiOjE3NTE4MzQ0MzF9.hY7PLaNrQifTgHUjg8Jb2899FQfCSoiGGJv6-yl6jS0"
    }
    """
)
public record RefreshResponse(
    @Schema(description = "서버에서 새로 발급한 Access Token")
    String accessToken,

    @Schema(description = "서버에서 새로 발급한 Refresh Token")
    String refreshToken
) {} 