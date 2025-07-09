package me.jinjjahalgae.presentation.api;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jinjjahalgae.domain.auth.dto.login.SocialLoginRequest;
import me.jinjjahalgae.domain.auth.dto.login.SocialLoginResponse;
import me.jinjjahalgae.domain.auth.usecase.interfaces.SocialLoginUseCase;
import me.jinjjahalgae.domain.auth.usecase.interfaces.LogoutUseCase;
import me.jinjjahalgae.domain.auth.usecase.interfaces.RefreshTokenUseCase;
import me.jinjjahalgae.domain.auth.dto.refresh.RefreshRequest;
import me.jinjjahalgae.domain.auth.dto.refresh.RefreshResponse;
import me.jinjjahalgae.global.common.CommonResponse;
import me.jinjjahalgae.global.security.jwt.JwtProperties;
import me.jinjjahalgae.global.security.jwt.CustomJwtPrincipal;
import me.jinjjahalgae.global.security.jwt.Token;
import me.jinjjahalgae.presentation.api.docs.auth.AuthControllerDocs;
import me.jinjjahalgae.presentation.util.CookieGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthControllerDocs {
    private final CookieGenerator cookieGenerator;
    private final JwtProperties jwtProperties;

    private final SocialLoginUseCase socialLoginUseCase;
    private final LogoutUseCase logoutUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;

    @Override
    @PostMapping("/login/social/body")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<SocialLoginResponse> socialLoginForBody(
            @Valid @RequestBody SocialLoginRequest req
    ) {
        SocialLoginResponse result = socialLoginUseCase.execute(req);

        return CommonResponse.success(result);
    }

    @Override
    @PostMapping("/login/social/cookie")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<String> socialLoginForCookie(
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

        return CommonResponse.success("로그인 성공");
    }

    @Override
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<Void> logout(
            @AuthenticationPrincipal CustomJwtPrincipal user
    ) {
        logoutUseCase.execute(user.getUserId());
        
        return CommonResponse.success();
    }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<RefreshResponse> refresh(
            @Valid @RequestBody RefreshRequest request
    ) {
        RefreshResponse result = refreshTokenUseCase.execute(request);
        
        return CommonResponse.success(result);
    }
}
