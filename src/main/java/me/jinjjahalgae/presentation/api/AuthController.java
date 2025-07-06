package me.jinjjahalgae.api;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.auth.dto.login.SocialLoginRequest;
import me.jinjjahalgae.domain.auth.dto.login.SocialLoginResponse;
import me.jinjjahalgae.domain.auth.enums.TokenDeliveryType;
import me.jinjjahalgae.domain.auth.usecase.interfaces.SocialLoginUseCase;
import me.jinjjahalgae.global.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final SocialLoginUseCase socialLoginUseCase;

    @PostMapping("/login/social/body")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SocialLoginResponse> socialLogin(
            @Valid @RequestBody SocialLoginRequest req
    ) {
        SocialLoginResponse result = socialLoginUseCase.execute(req);

        return ApiResponse.success(result);
    }

    @PostMapping("/login/social/cookie")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SocialLoginResponse> socialLogin(
            @Valid @RequestBody SocialLoginRequest req,
            HttpServletResponse httpServletResponse // 쿠키 담기 위함
    ) {
        SocialLoginResponse result = socialLoginUseCase.execute(req);

        // 옵션에 따라 분기처리
        if (req.tokenDeliveryType() == TokenDeliveryType.COOKIE) {
            // 3-1. 쿠키에 담아주기
            addTokenCookies(httpServletResponse, responseDto);
            // 성공했다는 의미로 간단한 메시지나 빈 Body와 함께 200 OK 응답
//            return ResponseEntity.ok().body("로그인에 성공했습니다.");
            return ApiResponse.success(result);
        }

        return ApiResponse.success(result);
    }
}
