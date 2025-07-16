package me.jinjjahalgae.domain.auth.mapper;

import me.jinjjahalgae.domain.auth.usecase.refresh.dto.RefreshResponse;
import me.jinjjahalgae.domain.auth.usecase.login.social.dto.SocialLoginResponse;
import me.jinjjahalgae.global.security.jwt.Token;
import org.springframework.stereotype.Component;

/**
 * Auth 도메인 매퍼
 */
@Component
public class AuthMapper {

    /**
     * Token을 SocialLoginResponse로 매핑
     */
    public SocialLoginResponse toSocialLoginResponse(Token token) {
        return new SocialLoginResponse(
            token.accessToken(),
            token.refreshToken()
        );
    }

    /**
     * Token을 RefreshResponse로 매핑
     */
    public RefreshResponse toRefreshResponse(Token token) {
        return new RefreshResponse(
            token.accessToken(),
            token.refreshToken()
        );
    }
} 