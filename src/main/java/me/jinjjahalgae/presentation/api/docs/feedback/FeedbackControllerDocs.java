package me.jinjjahalgae.presentation.api.docs.feedback;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.jinjjahalgae.domain.feedback.usecase.create.dto.CreateFeedbackRequest;
import me.jinjjahalgae.global.common.CommonResponse;
import me.jinjjahalgae.global.exception.ErrorResponse;
import me.jinjjahalgae.global.security.jwt.CustomJwtPrincipal;
import me.jinjjahalgae.presentation.api.docs.NoContentSwaggerResponse;

@Tag(name = "피드백 API", description = "피드백 관련 API")
public interface FeedbackControllerDocs {

    @Operation(
            summary = "피드백 생성",
            description = "감독자가 특정 인증에 대한 피드백을 생성합니다. <br> - 해당 계약에 참가중인 감독자만 피드백을 작성할 수 있습니다. <br> - 피드백 내용은 100자 이하여야 합니다. <br> - 피드백 상태는 `APPROVED`, `REJECTED` 중 하나여야 합니다.",
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "피드백 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = NoContentSwaggerResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                                    {
                                      "success": true,
                                      "result": null
                                    }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                @ExampleObject(
                                    name = "comment 누락",
                                    value = """
                                    {
                                      "success": false,
                                      "code": "BAD_REQUEST",
                                      "message": "comment는 필수입니다."
                                    }
                                    """
                                ),
                                @ExampleObject(
                                    name = "status 누락",
                                    value = """
                                    {
                                      "success": false,
                                      "code": "BAD_REQUEST",
                                      "message": "status는 필수입니다."
                                    }
                                    """
                                    ),
                                @ExampleObject(
                                    name = "잘못된 status 값",
                                    value = """
                                    {
                                      "success": false,
                                      "code": "BAD_REQUEST",
                                      "message": "status는 APPROVED, REJECTED 중 하나여야 합니다."
                                    }
                                    """
                                ),
                                @ExampleObject(
                                    name = "proofId 누락",
                                    value = """
                                    {
                                      "success": false,
                                      "code": "BAD_REQUEST",
                                      "message": "proofId는 필수입니다."
                                    }
                                    """
                                ),
                                @ExampleObject(
                                    name = "comment 길이 초과",
                                    value = """
                                    {
                                      "success": false,
                                      "code": "BAD_REQUEST",
                                      "message": "comment는 100자 이하여야 합니다."
                                    }
                                    """
                                )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                @ExampleObject(
                                    name = "유효하지 않은 토큰",
                                    value = """
                                    {
                                      "success": false,
                                      "code": "INVALID_TOKEN",
                                      "message": "유효하지 않은 토큰입니다."
                                    }"""
                                ),
                                @ExampleObject(
                                    name = "만료된 토큰",
                                    value = """
                                    {
                                      "success": false,
                                      "code": "EXPIRED_TOKEN",
                                      "message": "토큰이 만료되었습니다."
                                    }"""
                                )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "감독자 권한 없음",
                                    value = """
                                    {
                                      "success": false,
                                      "code": "PERMISSION_DENIED",
                                      "message": "접근 권한이 없습니다."
                                    }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 인증",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "존재하지 않는 인증",
                                    value = """
                                    {
                                      "success": false,
                                      "code": "PROOF_NOT_FOUND",
                                      "message": "해당 인증을 찾을 수 없습니다."
                                    }"""
                            )
                    )
            )
    })
//    @SecurityRequirement(name = "bearerAuth")
    CommonResponse<Void> createFeedback(
        @Parameter(hidden = true) CustomJwtPrincipal user,
        @Parameter(description = "피드백 생성 요청", required = true) CreateFeedbackRequest request
    );
} 