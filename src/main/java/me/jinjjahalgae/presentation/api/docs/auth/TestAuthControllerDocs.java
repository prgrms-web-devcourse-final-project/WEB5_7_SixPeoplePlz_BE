package me.jinjjahalgae.presentation.api.docs.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.jinjjahalgae.domain.auth.usecase.login.social.dto.SocialLoginResponse;
import me.jinjjahalgae.global.common.CommonResponse;

@Tag(name = "Test Auth API", description = "테스트 유저 JWT 발급 (프론트 개발용)")
public interface TestAuthControllerDocs {
    @Operation(
        summary = "테스트 유저 JWT 발급 (프론트 개발용)",
        description = "소셜로그인 없이 테스트 유저를 생성하고 JWT를 반환합니다. <br> 프론트 개발/테스트용 임시 API입니다. <br> nickname, email 등은 임시로 자동 생성되고, 한번 테스트유저가 생성되면 그 계정을 계속 이용합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "테스트 유저 JWT 발급 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SocialLoginResponse.class),
                examples = @ExampleObject(
                    name = "토큰 발급 성공",
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
        )
    })
    CommonResponse<SocialLoginResponse> testLogin();
} 