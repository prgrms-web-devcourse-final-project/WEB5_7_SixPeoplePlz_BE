package me.jinjjahalgae.presentation.api.docs.auth.login;

import me.jinjjahalgae.domain.auth.dto.login.SocialLoginResponse;
import me.jinjjahalgae.global.common.CommonResponse;

public class SocialLoginBodySwaggerResponse extends CommonResponse<SocialLoginResponse> {
    public SocialLoginBodySwaggerResponse(SocialLoginResponse result) {
        super(result);
    }
}