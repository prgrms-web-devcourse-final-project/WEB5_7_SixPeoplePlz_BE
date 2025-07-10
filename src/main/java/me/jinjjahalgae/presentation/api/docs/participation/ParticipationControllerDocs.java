package me.jinjjahalgae.presentation.api.docs.participation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import me.jinjjahalgae.domain.participation.dto.request.ParticipationCreateRequest;
import me.jinjjahalgae.global.common.CommonResponse;
import me.jinjjahalgae.global.exception.ErrorResponse;
import me.jinjjahalgae.global.security.jwt.CustomJwtPrincipal;
import me.jinjjahalgae.presentation.api.docs.NoContentSwaggerResponse;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "참여 API", description = "감독 참여, 철회, 중도 포기 관련 API")
public interface ParticipationControllerDocs {

    @Operation(
            summary = "감독으로 계약 참여 (서명)",
            description = "초대받은 계약에 감독으로 참여하고 서명을 등록합니다. 로그인이 필요합니다.",
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "감독 참여 성공",
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
                            examples = @ExampleObject(
                                    name = "이미 참여한 계약에 서명한 경우",
                                    value = """
                                    {
                                      "success": false,
                                      "code": "INVITE_ALREADY_PARTICIPATED",
                                      "message": "이미 참여한 계약입니다."
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
                            examples = {
                                    @ExampleObject(name = "인증 실패", value = """
                                    {
                                      "success": false,
                                      "code": "INVALID_TOKEN",
                                      "message": "유효하지 않은 토큰입니다."
                                    }
                                    """),
                                    @ExampleObject(name = "만료된 토큰", value = """
                                    {
                                      "success": false,
                                      "code": "EXPIRED_TOKEN",
                                      "message": "토큰이 만료되었습니다."
                                    }
                                    """)
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 계약",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "계약이 존재하지 않는 경우",
                                    value = """
                                    {
                                      "success": false,
                                      "code": "CONTRACT_NOT_FOUND",
                                      "message": "존재하지 않는 계약입니다."
                                    }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "감독자 인원 초과",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "감독자의 인원이 5명 다 채워진 경우",
                                    value = """
                                    {
                                      "success": false,
                                      "code": "SUPERVISOR_ALREADY_FULL",
                                      "message": "이미 5명의 감독자가 참여했습니다."
                                    }"""
                            )
                    )
            )
    })
    CommonResponse<Void> joinAsSupervisor(
            @Parameter(description = "계약 ID", example = "1") Long contractId,
            @Valid @RequestBody ParticipationCreateRequest request,
            @Parameter(hidden = true) CustomJwtPrincipal principal);

    @Operation(
            summary = "감독 참여 철회 (계약 시작 전)",
            description = "시작 전인 계약에 대한 감독 참여를 완전히 철회합니다. 참여 기록이 삭제됩니다. 로그인이 필요합니다.",
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "감독 철회 성공",
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
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "인증 실패", value = """
                                    {
                                      "success": false,
                                      "code": "INVALID_TOKEN",
                                      "message": "유효하지 않은 토큰입니다."
                                    }
                                    """),
                                    @ExampleObject(name = "만료된 토큰", value = """
                                    {
                                      "success": false,
                                      "code": "EXPIRED_TOKEN",
                                      "message": "토큰이 만료되었습니다."
                                    }
                                    """)
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "데이터 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "계약이 존재하지 않는 경우", summary = "계약서 없음", value = """
                                    {
                                      "success": false,
                                      "code": "CONTRACT_NOT_FOUND",
                                      "message": "존재하지 않는 계약입니다."
                                    }
                                    """),
                                    @ExampleObject(name = "해당 계약에 감독으로 참여하고 있지 않은 경우", summary = "감독 참여 정보 없음", value = """
                                    {
                                      "success": false,
                                      "code": "SUPERVISOR_PARTICIPATION_NOT_FOUND",
                                      "message": "해당 계약에 감독으로 참여하고 있지 않습니다."
                                    }
                                    """)
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "잘못된 계약 상태",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "계약이 시작되기 전에만 감독 철회 가능",
                                    value = """
                                    {
                                      "success": false,
                                      "code": "CANNOT_WITHDRAW_PARTICIPATION_AFTER_START",
                                      "message": "계약이 시작되기 전에만 감독 계약을 철회할 수 있습니다."
                                    }"""
                            )
                    )
            )
    })
    CommonResponse<Void> withdrawAsSupervisor(
            @Parameter(description = "계약 ID", example = "1") Long contractId,
            @Parameter(hidden = true) CustomJwtPrincipal principal);


    @Operation(
            summary = "감독 중도 포기 (계약 진행 중)",
            description = "진행 중인 계약의 감독 역할을 포기합니다. 참여 기록은 남고 상태만 변경됩니다. 로그인이 필요합니다.",
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "감독 중도 포기 성공",
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
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "인증 실패", value = """
                                    {
                                      "success": false,
                                      "code": "INVALID_TOKEN",
                                      "message": "유효하지 않은 토큰입니다."
                                    }
                                    """),
                                    @ExampleObject(name = "만료된 토큰", value = """
                                    {
                                      "success": false,
                                      "code": "EXPIRED_TOKEN",
                                      "message": "토큰이 만료되었습니다."
                                    }
                                    """)
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "데이터 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "계약이 존재하지 않는 경우", summary = "계약서 없음", value = """
                                    {
                                      "success": false,
                                      "code": "CONTRACT_NOT_FOUND",
                                      "message": "존재하지 않는 계약입니다."
                                    }
                                    """),
                                    @ExampleObject(name = "해당 계약에 감독으로 참여하고 있지 않은 경우", summary = "감독 참여 정보 없음", value = """
                                    {
                                      "success": false,
                                      "code": "SUPERVISOR_PARTICIPATION_NOT_FOUND",
                                      "message": "해당 계약에 감독으로 참여하고 있지 않습니다."
                                    }
                                    """)
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "잘못된 계약 상태",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "계약 진행중에만 감독 중도 포기 가능",
                                    value = """
                                    {
                                      "success": false,
                                      "code": "CANNOT_ABANDON_PARTICIPATION_UNLESS_IN_PROGRESS",
                                      "message": "계약 진행중에만 감독을 중도 포기할 수 있습니다."
                                    }"""
                            )
                    )
            )
    })
    CommonResponse<Void> abandonAsSupervisor(
            @Parameter(description = "계약 ID", example = "1") Long contractId,
            @Parameter(hidden = true) CustomJwtPrincipal principal);
}