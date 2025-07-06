package me.jinjjahalgae.domain.auth.usecase.interfaces;

import me.jinjjahalgae.domain.auth.dto.login.SocialLoginRequest;
import me.jinjjahalgae.domain.auth.dto.login.SocialLoginResponse;
import me.jinjjahalgae.domain.common.UseCase;

public interface SocialLoginUseCase extends UseCase<SocialLoginRequest, SocialLoginResponse> {
    SocialLoginResponse execute(SocialLoginRequest req);
}
