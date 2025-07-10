package me.jinjjahalgae.domain.auth.usecase.login.social;

import me.jinjjahalgae.domain.auth.usecase.login.social.dto.SocialLoginRequest;
import me.jinjjahalgae.domain.auth.usecase.login.social.dto.SocialLoginResponse;

public interface SocialLoginUseCase{
    SocialLoginResponse execute(SocialLoginRequest req);
}
