package me.jinjjahalgae.domain.auth.usecase.social_login;

import me.jinjjahalgae.domain.auth.usecase.social_login.dto.SocialLoginRequest;
import me.jinjjahalgae.domain.auth.usecase.social_login.dto.SocialLoginResponse;

public interface SocialLoginUseCase{
    SocialLoginResponse execute(SocialLoginRequest req);
}
