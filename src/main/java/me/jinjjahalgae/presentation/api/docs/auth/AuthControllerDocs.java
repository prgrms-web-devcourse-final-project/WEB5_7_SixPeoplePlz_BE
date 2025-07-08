package me.jinjjahalgae.presentation.api.docs.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import me.jinjjahalgae.domain.auth.dto.login.SocialLoginRequest;
import me.jinjjahalgae.domain.auth.dto.login.SocialLoginResponse;
import me.jinjjahalgae.global.common.CommonResponse;
import me.jinjjahalgae.global.exception.ErrorResponse;
import me.jinjjahalgae.global.security.jwt.CustomJwtPrincipal;
import me.jinjjahalgae.presentation.api.docs.auth.login.SocialLoginBodySwaggerResponse;
import me.jinjjahalgae.presentation.api.docs.auth.login.SocialLoginCookieSwaggerResponse;
import me.jinjjahalgae.domain.auth.dto.refresh.RefreshRequest;
import me.jinjjahalgae.domain.auth.dto.refresh.RefreshResponse;
import me.jinjjahalgae.presentation.api.docs.auth.refresh.RefreshSwaggerResponse;

@Tag(name = "인증 API", description = "소셜 로그인 등 인증 관련 API")
public interface AuthControllerDocs {

    @Operation(
            summary = "소셜 로그인 (Body 방식)",
            description = "소셜 플랫폼의 Access Token으로 로그인/회원가입을 처리하고, 자체 JWT를 응답 Body에 담아 반환합니다. <br> - 현재 provider는 **[KAKAO | NAVER]** 만 허용됩니다.<br> - accessToken 만료시간 30분<br> - refreshToken 만료시간 30일"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "로그인/회원가입 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SocialLoginBodySwaggerResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                                    {
                                      "success": true,
                                      "result": {
                                        "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI2IiwiaWF0IjoxNzUxODMyNjMxLCJleHAiOjE3NTE4MzQ0MzF9.hY7PLaNrQifTgHUjg8Jb2899FQfCSoiGGJv6-yl6jS0",
                                        "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI2IiwiaWF0IjoxNzUxODMyNjMxLCJleHAiOjE3NTE4MzQ0MzF9.hY7PLaNrQifTgHUjg8Jb2899FQfCSoiGGJv6-yl6jS0"
                                      }
                                    }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (provider 누락 또는 accessToken 누락 등)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                @ExampleObject(
                                    name = "잘못된 provider, provider 누락, 공백",
                                    value = """
                                    {
                                      "success": false,
                                      "code": "BAD_REQUEST",
                                      "message": "provider는 필수입니다."
                                    }
                                    """
                                ),
                                @ExampleObject(
                                    name = "accessToken 누락",
                                    value = """
                                    {
                                      "success": false,
                                      "code": "BAD_REQUEST",
                                      "message": "accessToken은 필수입니다."
                                    }
                                    """
                                )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "유효하지 않은 소셜 AccessToken",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "인증 실패",
                                    value = """
                                    {
                                      "success": false,
                                      "code": "INVALID_TOKEN",
                                      "message": "유효하지 않은 토큰입니다."
                                    }"""
                            )
                    )
            )
    })
    CommonResponse<SocialLoginResponse> socialLoginForBody(
            @Parameter(description = "소셜 로그인 정보", required = true) SocialLoginRequest req
    );

    @Operation(
            summary = "소셜 로그인 (Cookie 방식)",
            description = "소셜 플랫폼의 Access Token으로 로그인/회원가입을 처리하고, 자체 JWT를 HttpOnly 쿠키에 담아 반환합니다. 응답 Body에는 **성공 메시지만 포함** 됩니다. <br> - 현재 provider는 **[KAKAO | NAVER]** 만 허용됩니다. <br> - accessToken 만료시간 30분<br> - refreshToken 만료시간 30일"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "로그인/회원가입 성공. 토큰은 쿠키로 설정됨",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SocialLoginCookieSwaggerResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                                    {
                                      "success": true,
                                      "result": "로그인 성공"
                                    }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (provider 또는 accessToken 누락 등)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                @ExampleObject(
                                    name = "잘못된 provider, provider 누락, 공백",
                                    value = """
                                    {
                                      "success": false,
                                      "code": "BAD_REQUEST",
                                      "message": "provider는 필수입니다."
                                    }
                                    """
                                ),
                                @ExampleObject(
                                    name = "accessToken 누락, 공백",
                                    value = """
                                    {
                                      "success": false,
                                      "code": "BAD_REQUEST",
                                      "message": "accessToken은 필수입니다."
                                    }
                                    """
                                )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "유효하지 않은 소셜 AccessToken",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "인증 실패",
                                    value = """
                                    {
                                      "success": false,
                                      "code": "INVALID_TOKEN",
                                      "message": "유효하지 않은 토큰입니다."
                                    }"""
                            )
                    )
            )
    })
    CommonResponse<String> socialLoginForCookie(
            @Parameter(description = "소셜 로그인 정보", required = true) SocialLoginRequest req,
            HttpServletResponse servletResponse
    );

    @Operation(
            summary = "로그아웃",
            description = "로그아웃 처리. 리프레시 토큰을 무효화합니다. <br> result는 null <br>인증 필요.",
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "로그아웃 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SocialLoginCookieSwaggerResponse.class),
                    examples = @ExampleObject(
                            name = "로그아웃 성공",
                            value = """
                            {
                              "success": true,
                              "result": null
                            }"""
                    )
            )
        ),
        @ApiResponse(
                responseCode = "401",
                description = "유효하지 않은 토큰",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class),
                        examples = @ExampleObject(
                                name = "인증 실패",
                                value = """
                                {
                                  "success": false,
                                  "code": "INVALID_TOKEN",
                                  "message": "유효하지 않은 토큰입니다."
                                }"""
                        )
                )
        ),
        @ApiResponse(
                responseCode = "404",
                description = "존재하지 않는 유저",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class),
                        examples = @ExampleObject(
                                name = "잘못된 인증 정보",
                                value = """
                            {
                              "success": false,
                              "code": "USER_NOT_FOUND",
                              "message": "존재하지 않는 유저입니다."
                            }"""
                        )
                )
        )
    })
    CommonResponse<Void> logout(
        @Parameter(hidden = true) CustomJwtPrincipal user
    );

    @Operation(
        summary = "토큰 재발급",
        description = "리프레시 토큰으로 access/refresh 토큰을 재발급합니다. <br> - accessToken 만료시간 30분<br> - refreshToken 만료시간 30일"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "토큰 재발급 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RefreshSwaggerResponse.class),
                examples = @ExampleObject(
                    name = "토큰 재발급 성공",
                    value = """
                    {
                      "success": true,
                      "result": {
                        "accessToken": "eyJhbGc123451NiJ9.eyJzdWIiOiI2IiwiaWF0IjoxNzUxODMyNjMxLCJleHAiOjE3NTE4MzQ0MzF9.hY7PLaNrQifTgHUjg8Jb2899FQfCSoiGGJv6-yl6jS0",
                        "refreshToken": "eyJhbGc123451NiJ9.eyJzdWIiOiI2IiwiaWF0IjoxNzUxODMyNjMxLCJleHAiOjE3NTE4MzQ0MzF9.hY7PLaNrQifTgHUjg8Jb2899FQfCSoiGGJv6-yl6jS0"
                      }
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (refreshToken 누락 등)",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    name = "잘못된 요청",
                    value = """
                    {
                      "success": false,
                      "code": "BAD_REQUEST",
                      "message": "refreshToken은 필수입니다."
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "유효하지 않은 리프레시 토큰",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    name = "유효하지 않은 토큰",
                    value = """
                    {
                      "success": false,
                      "code": "INVALID_TOKEN",
                      "message": "유효하지 않은 리프레시 토큰입니다."
                    }
                    """
                )
            )
        )
    })
    CommonResponse<RefreshResponse> refresh(
        @Parameter(description = "리프레시 토큰 요청 DTO", required = true) RefreshRequest request
    );
}
