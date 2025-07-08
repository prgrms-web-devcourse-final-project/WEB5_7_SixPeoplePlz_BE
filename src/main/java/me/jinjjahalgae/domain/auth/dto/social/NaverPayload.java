package me.jinjjahalgae.domain.auth.dto.social;

// 네이버 API 응답을 담을 객체
public record NaverPayload(
        String resultcode, // 디버깅용
        Response response // 네이버 계정 정보
) {
    public record Response(
            String id, // 네이버 유저 id
            String email, // 이메일
            String nickname, // 별명
            String name // 이름
    ) {}
}
