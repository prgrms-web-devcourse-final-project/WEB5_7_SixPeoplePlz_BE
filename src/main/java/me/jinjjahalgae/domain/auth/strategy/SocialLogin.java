package me.jinjjahalgae.domain.auth.strategy;

import me.jinjjahalgae.domain.auth.model.SocialProfile;
import me.jinjjahalgae.domain.auth.enums.Provider;

public interface SocialLogin {
    Provider getProvider();

    SocialProfile getUserInfo(String accessToken); // 소셜 사용자 정보 조회
}
