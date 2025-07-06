package me.jinjjahalgae.domain.auth.dto.login;

/**
 * 로그인 이후 반환할 JWT 응답 dto
 */
public record SocialLoginResponse(
        String accessToken, // 서버 accessToken
        String refreshToken // 서버 refreshToken
) {
}
