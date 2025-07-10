package me.jinjjahalgae.domain.auth.usecase.login.social.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 로그인 이후 반환할 JWT 응답 dto
 */
@Schema(
        title = "소셜로그인 응답",
        description = "로그인 성공 시 JWT 응답 DTO",
        example = """
        {
          "accessToken": "eyJhbGc123451NiJ9.eyJzdWIiOiI2IiwiaWF0IjoxNzUxODMyNjMxLCJleHAiOjE3NTE4MzQ0MzF9.hY7PLaNrQifTgHUjg8Jb2899FQfCSoiGGJv6-yl6jS0",
          "refreshToken": "eyJhbGc123451NiJ9.eyJzdWIiOiI2IiwiaWF0IjoxNzUxODMyNjMxLCJleHAiOjE3NTE4MzQ0MzF9.hY7PLaNrQifTgHUjg8Jb2899FQfCSoiGGJv6-yl6jS0"
        }
        """
)


public record SocialLoginResponse(
        @Schema(description = "서버에서 발급한 Access Token")
        String accessToken, // 서버 accessToken

        @Schema(description = "서버에서 발급한 Refresh Token")
        String refreshToken // 서버 refreshToken
) {
}
