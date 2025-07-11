package me.jinjjahalgae.domain.auth.usecase.login.social.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 로그인 할 때 OAuth 증명을 검증하기 위한 요청 dto
 */
@Schema(
        title = "소셜로그인 요청",
        description = "소셜 로그인 요청 DTO"
)
public record SocialLoginRequest(
        @Schema(
                description = "소셜 로그인 제공자 (NAVER, KAKAO)",
                requiredMode = Schema.RequiredMode.REQUIRED,
                example = "NAVER"
        )
        @NotNull(message = "provider는 필수입니다.")
        String provider, // 써드파티 제공자 (NAVER, KAKAO)

        @Schema(
                description = "써드파티로부터 받은 Access Token",
                requiredMode = Schema.RequiredMode.REQUIRED,
                example = "r3bas52789CPG-7jV9t3ht6XPwJKHCT9AABAEAQoXBi4AAAGX4VCtCK-b-4ep6DEo"
        )
        @NotBlank(message = "accessToken은 필수입니다.")
        String accessToken // 써드파티 토큰
) {
}
