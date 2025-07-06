package me.jinjjahalgae.domain.auth.util;

import org.springframework.stereotype.Component;

@Component
public class CookieGenerator {
    public Cookie createCookie(String key, String value, int maxAge) {
        Cookie cookie = new Cookie(key, value);
        cookie.setHttpOnly(true); // JavaScript에서 접근 불가
        cookie.setSecure(true);   // HTTPS 통신에서만 전송
        cookie.setPath("/");      // 모든 경로에서 쿠키 사용
        cookie.setMaxAge(maxAge); // 쿠키 만료 시간 설정 (초 단위)
        return cookie;
    }
}
