package me.jinjjahalgae.presentation.api;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jinjjahalgae.domain.auth.dto.login.SocialLoginRequest;
import me.jinjjahalgae.domain.auth.dto.login.SocialLoginResponse;
import me.jinjjahalgae.domain.auth.usecase.interfaces.SocialLoginUseCase;
import me.jinjjahalgae.global.common.ApiResponse;
import me.jinjjahalgae.global.security.jwt.JwtProperties;
import me.jinjjahalgae.presentation.util.CookieGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final SocialLoginUseCase socialLoginUseCase;
    private final CookieGenerator cookieGenerator;
    private final JwtProperties jwtProperties;

    @PostMapping("/login/social/body")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SocialLoginResponse> socialLoginForBody(
            @Valid @RequestBody SocialLoginRequest req
    ) {
        SocialLoginResponse result = socialLoginUseCase.execute(req);

        return ApiResponse.success(result);
    }

    @PostMapping("/login/social/cookie")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<String> socialLoginForCookie(
            @Valid @RequestBody SocialLoginRequest req,
            HttpServletResponse servletResponse // 쿠키 담기 위함
    ) {
        SocialLoginResponse result = socialLoginUseCase.execute(req);

        int accessTokenMaxAge = (int) jwtProperties.getAccessTokenExpiration().toSeconds();
        int refreshTokenMaxAge = (int) jwtProperties.getRefreshTokenExpiration().toSeconds();

        Cookie accessTokenCookie = cookieGenerator.createCookie(
                "accessToken", result.accessToken(), accessTokenMaxAge);
        Cookie refreshTokenCookie = cookieGenerator.createCookie(
                "refreshToken", result.refreshToken(), refreshTokenMaxAge);

        servletResponse.addCookie(accessTokenCookie);
        servletResponse.addCookie(refreshTokenCookie);

        return ApiResponse.success("로그인 성공");
    }
}
