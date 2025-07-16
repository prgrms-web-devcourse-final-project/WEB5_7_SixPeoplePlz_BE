package me.jinjjahalgae.domain.auth.dto.social;

import com.fasterxml.jackson.annotation.JsonProperty;

// 카카오 API 응답을 담을 객체
public record KakaoPayload(
        Long id, // 카카오 유저 id
        @JsonProperty("kakao_account") KakaoAccount kakaoAccount // 카카오계정 정보
) {
    public record KakaoAccount(
            Profile profile, // 프로필 정보
            String email // 카카오 이메일
    ) {}

    public record Profile(
            String nickname // 닉네임
    ) {}
}