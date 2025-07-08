package me.jinjjahalgae.domain.auth.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "소셜 로그인 제공자",
        example = "NAVER",
        enumAsRef = true
)
public enum Provider {
    KAKAO,
    NAVER
}
