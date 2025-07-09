package me.jinjjahalgae.domain.auth.dto.social;

/**
 * 소셜 로그인 페이로드 응답을 진짜할게에 맞게 표준화
 */
public record SocialProfile(
        String socialId, // 소셜 식별자 id
        String email, // (Optional) email
        String nickname, // 닉네임
        String name // 이름 (만약 있다면 이름 적용, 없다면 닉네임)
) {
}