package me.jinjjahalgae.presentation.api.docs.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.jinjjahalgae.domain.user.dto.MyInfoResponse;
import me.jinjjahalgae.domain.user.dto.UpdateMyInfoRequest;
import me.jinjjahalgae.global.common.CommonResponse;
import me.jinjjahalgae.global.exception.ErrorResponse;
import me.jinjjahalgae.global.security.jwt.CustomJwtPrincipal;
import me.jinjjahalgae.presentation.api.docs.NoContentSwaggerResponse;

@Tag(name = "유저 API", description = "내 정보 조회/수정/탈퇴 API")
public interface UserControllerDocs {

    @Operation(
        summary = "내 정보 조회",
        description = "인증된 사용자의 정보를 조회합니다.",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MyInfoSwaggerResponse.class),
                examples = @ExampleObject(
                    name = "성공 응답",
                    value = """
                    {
                      "success": true,
                      "result": {
                        "id": 1,
                        "name": "홍길동",
                        "nickname": "홍길동",
                        "email": "user@example.com"
                      }
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "인증 실패",
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
                    }
                    """
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
    CommonResponse<MyInfoResponse> getMyInfo(
        @Parameter(hidden = true) CustomJwtPrincipal user
    );

    @Operation(
        summary = "내 정보 수정",
        description = "인증된 사용자의 닉네임을 수정합니다. <br> - nickname",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "수정 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MyInfoSwaggerResponse.class),
                examples = @ExampleObject(
                    name = "성공 응답",
                    value = """
                    {
                      "success": true,
                      "result": {
                        "id": 1,
                        "name": "홍길동",
                        "nickname": "수정된닉네임",
                        "email": "user@example.com"
                      }
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (nickname이 없거나 공백)",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    name = "닉네임 누락",
                    value = """
                    {
                      "success": false,
                      "code": "BAD_REQUEST",
                      "message": "nickname은 필수입니다."
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "인증 실패",
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
                    }
                    """
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
    CommonResponse<MyInfoResponse> updateMyInfo(
        @Parameter(hidden = true) CustomJwtPrincipal user,
        @Parameter(description = "수정 요청 DTO", required = true) UpdateMyInfoRequest request
    );

    @Operation(
        summary = "회원 탈퇴",
        description = "인증된 사용자의 계정을 삭제합니다.",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "탈퇴 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = NoContentSwaggerResponse.class),
                        examples = @ExampleObject(
                                name = "탈퇴 성공",
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
            description = "인증 실패",
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
                    }
                    """
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
    CommonResponse<Void> deleteMyAccount(
        @Parameter(hidden = true) CustomJwtPrincipal user
    );
} 