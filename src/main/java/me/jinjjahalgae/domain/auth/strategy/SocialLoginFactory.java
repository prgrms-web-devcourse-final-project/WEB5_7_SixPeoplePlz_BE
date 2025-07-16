package me.jinjjahalgae.domain.auth.strategy;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.auth.enums.Provider;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SocialLoginFactory {
    private final KakaoLogin kakaoLogin;
    private final NaverLogin naverLogin;

    /**
     * Provider에 맞는 로그인 전략 반환
     */
    public SocialLogin getStrategy(String provider) {
        try {
            Provider p = Provider.valueOf(provider.toUpperCase());

            return switch (p) {
                case KAKAO -> kakaoLogin;
                case NAVER -> naverLogin;
            };
        } catch (IllegalArgumentException e) {
            throw ErrorCode.INVALID_PROVIDER.serviceException("유효하지 않은 provider: " + provider);
        }
    }
}
