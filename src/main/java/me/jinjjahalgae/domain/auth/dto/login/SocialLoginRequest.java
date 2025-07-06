package me.jinjjahalgae.domain.auth.dto.login;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 로그인 할 때 OAuth 증명을 검증하기 위한 요청 dto
 */
public record SocialLoginRequest(
        @NotNull String provider, // 써드파티 제공자 (NAVER, KAKAO)
        @NotBlank String accessToken // 써드파티 토큰
) {
}
