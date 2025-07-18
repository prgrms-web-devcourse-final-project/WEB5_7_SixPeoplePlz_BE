package me.jinjjahalgae.presentation.api;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.auth.usecase.login.social.dto.SocialLoginResponse;
import me.jinjjahalgae.domain.auth.usecase.testlogin.TestLoginUseCase;
import me.jinjjahalgae.global.common.CommonResponse;
import me.jinjjahalgae.presentation.api.docs.auth.TestAuthControllerDocs;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Profile({"local", "dev"})
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class TestAuthController implements TestAuthControllerDocs {
    private final TestLoginUseCase testLoginUseCase;

    @PostMapping("/test-login")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<SocialLoginResponse> testLogin() {
        return CommonResponse.success(testLoginUseCase.execute());
    }
} 