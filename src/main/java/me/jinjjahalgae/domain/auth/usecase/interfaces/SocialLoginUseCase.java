package me.jinjjahalgae.domain.auth.usecase.interfaces;

import me.jinjjahalgae.domain.auth.dto.login.SocialLoginRequest;
import me.jinjjahalgae.domain.auth.dto.login.SocialLoginResponse;

public interface SocialLoginUseCase{
    SocialLoginResponse execute(SocialLoginRequest req);
}
