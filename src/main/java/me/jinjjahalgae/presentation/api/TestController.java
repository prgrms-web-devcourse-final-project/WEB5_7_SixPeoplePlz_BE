package me.jinjjahalgae.presentation.api;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.global.common.CommonResponse;
import me.jinjjahalgae.global.security.jwt.CustomJwtPrincipal;
import me.jinjjahalgae.global.security.jwt.JwtTokenProvider;
import me.jinjjahalgae.global.security.jwt.Token;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Profile("dev")
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/token")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<Token> getTestToken() {
        return CommonResponse.success(jwtTokenProvider.generateToken(1L));
    }

    @GetMapping("/token2")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<Token> getTestToken2() {
        return CommonResponse.success(jwtTokenProvider.generateToken(2L));
    }

    @GetMapping("/me")
    public CommonResponse<Long> getMyInfo(@AuthenticationPrincipal CustomJwtPrincipal principal) {
        return CommonResponse.success(principal.getUserId());
    }
}