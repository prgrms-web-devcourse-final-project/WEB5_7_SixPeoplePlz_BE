package me.jinjjahalgae.presentation.api.docs.invite;

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
import me.jinjjahalgae.domain.invite.usecase.verify.password.dto.VerifyInvitePasswordRequest;
import me.jinjjahalgae.domain.invite.usecase.verify.password.dto.ContractUuidResponse;
import me.jinjjahalgae.domain.invite.usecase.get.contract.dto.InviteContractInfoResponse;
import me.jinjjahalgae.domain.invite.usecase.create.invite.dto.InviteLinkResponse;
import me.jinjjahalgae.global.common.CommonResponse;
import me.jinjjahalgae.global.exception.ErrorResponse;
import me.jinjjahalgae.global.security.jwt.CustomJwtPrincipal;
import me.jinjjahalgae.presentation.api.docs.NoContentSwaggerResponse;

@Tag(name = "초대 API", description = "초대 링크 및 계약서 조회 관련 API")
public interface InviteControllerDocs {

    @Operation(summary = "초대링크 생성", description = "특정 계약에 대한 초대링크와 임시 비밀번호를 생성합니다. 로그인이 필요합니다.",
            security = { @SecurityRequirement(name = "bearerAuth") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "초대링크 생성 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateInviteLinkSwaggerResponse.class),
                            examples = @ExampleObject(name = "성공 응답", value = """
                                 {
                                   "success": true,
                                   "result": {
                                     "inviteUrl": "https://jinjjahalgae.xyz/api/invite/da2316d7",
                                     "password": "ac08ee16"
                                   }
                                 }"""))),
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
            @ApiResponse(responseCode = "403", description = "자신의 계약이 아닌 계약의 초대링크 생성을 요청한 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "자신의 계약이 아닌데 초대코드 생성 요청", value = """
                                 { "success": false, "code": "ACCESS_DENIED", "message": "자신의 계약에만 초대링크 생성이 가능합니다." }
                                 """))),
            @ApiResponse(responseCode = "404", description = "초대링크 생성을 요청한 계약이 존재하지 않는 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "계약이 존재하지 않음", value = """
                                 { "success": false, "code": "CONTRACT_NOT_FOUND", "message": "존재하지 않는 계약입니다." }
                                 """))),
            @ApiResponse(responseCode = "409", description = "초대링크 생성을 요청한 계약이 대기 상태가 아닌경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "계약 시작 전에만 초대링크 생성 가능", value = """
                                 { "success": false, "code": "CANNOT_CREATE_INVITE_AFTER_START", "message": "계약이 시작히기 전에만 초대 코드를 생성할 수 있습니다." }
                                 """)))
    })
    CommonResponse<InviteLinkResponse> createInviteLink(
            @Parameter(description = "계약 ID", example = "1") Long contractId,
            @Parameter(hidden = true) CustomJwtPrincipal principal);


    @Operation(summary = "초대링크 유효성 검사", description = "초대 코드가 유효한지(만료되지 않았는지) 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "초대링크 유효함",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = NoContentSwaggerResponse.class),
                            examples = @ExampleObject(name = "성공 응답", value = """
                                 { "success": true, "result": null }
                                 """))),
            @ApiResponse(responseCode = "404", description = "존재하지 않거나 만료된 초대링크",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "존재하지 않거나 만료된 초대링크", value = """
                                 { "success": false, "code": "INVITE_NOT_FOUND", "message": "존재하지 않거나 만료된 초대입니다." }
                                 """)))
    })
    CommonResponse<Void> checkInviteLink(
            @Parameter(description = "초대 코드", example = "da2316d7") String inviteCode);


    @Operation(summary = "초대링크 비밀번호 확인", description = "입력된 비밀번호가 유효한지 검증하고, 성공 시 계약 UUID를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 검증 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VerifyPasswordSwaggerResponse.class),
                            examples = @ExampleObject(name = "성공 응답", value = """
                                 {
                                   "success": true,
                                   "result": {
                                     "contractUuid": "36865103-5d08-4139-ba4a-b32da2316d7f"
                                   }
                                 }"""))),
            @ApiResponse(responseCode = "401", description = "비밀번호 불일치",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "초대코드에 해당하는 비밀번호가 일치하지 않음", value = """
                                 { "success": false, "code": "INVALID_INVITE_PASSWORD", "message": "초대 비밀번호가 일치하지 않습니다." }
                                 """))),
            @ApiResponse(responseCode = "404", description = "존재하지 않거나 만료된 초대링크",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "존재하지 않거나 만료된 초대링크", value = """
                                 { "success": false, "code": "INVITE_NOT_FOUND", "message": "존재하지 않거나 만료된 초대입니다." }
                                 """)))
    })
    CommonResponse<ContractUuidResponse> verifyPassword(
            @Parameter(description = "초대 코드", example = "da2316d7") String inviteCode,
            @Valid VerifyInvitePasswordRequest request);


    @Operation(summary = "초대 계약서 상세 조회", description = "비밀번호 검증 후 계약서의 상세 정보를 조회합니다. 로그인이 필요합니다.",
            security = { @SecurityRequirement(name = "bearerAuth") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "계약서 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GetInviteContractInfoSwaggerResponse.class),
                            examples = @ExampleObject(name = "성공 응답", value = """
                                 {
                                   "success": true,
                                   "result": {
                                     "contractorName": "홍길동",
                                     "contractorSignatureKey": "36865103-5d08-4139-ba4a-b32da2316d7f",
                                     "uuid": "36865103-5d08-4139-ba4a-b32da2316d7f",
                                     "startDate": "2025-07-01T00:00:00",
                                     "endDate": "2025-07-31T23:59:59",
                                     "title": "매일 아침 30분 운동하기",
                                     "goal": "한 달 동안 매일 아침 조깅을 하여 체력을 증진한다.",
                                     "penalty": "실패 시 친구에게 커피 사주기",
                                     "reward": "성공 시 나에게 선물 사주기",
                                     "life": 3,
                                     "totalProof": 30,
                                     "totalSupervisor": 2,
                                     "oneOff": false,
                                     "status": "PENDING",
                                     "type": "BASIC",
                                     "supervisorInfos": [
                                       { "supervisorName": "김감독", "supervisorSignatureKey": null },
                                       { "supervisorName": "박감시", "supervisorSignatureKey": null }
                                     ]
                                   }
                                 }"""))),
            @ApiResponse(responseCode = "400", description = "요청한 사용자가 이미 계약에 참여한 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "이미 계약에 참여한 사용자", value = """
                                 { "success": false, "code": "INVITE_ALREADY_PARTICIPATED", "message": "이미 참여한 계약입니다." }
                                 """))),
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
            @ApiResponse(responseCode = "404", description = "초대링크에 해당하는 계약이 없거나 계약의 계약자가 없는 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "계약서가 존재하지 않음", summary = "계약서 없음", value = """
                                                 { "success": false, "code": "CONTRACT_NOT_FOUND", "message": "존재하지 않는 계약입니다." }
                                                 """),
                                    @ExampleObject(name = "CONTRACTOR_PARTICIPATION_NOT_FOUND", summary = "계약자 정보 없음", value = """
                                                 { "success": false, "code": "CONTRACTOR_PARTICIPATION_NOT_FOUND", "message": "계약자의 정보가 없는 계약입니다." }
                                                 """)
                            })),
            @ApiResponse(responseCode = "409", description = "이미 5명의 감독자가 전부 채워진 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "SUPERVISOR_ALREADY_FULL", value = """
                                 { "success": false, "code": "SUPERVISOR_ALREADY_FULL", "message": "이미 5명의 감독자가 참여했습니다." }
                                 """)))
    })
    CommonResponse<InviteContractInfoResponse> getContractInfo(
            @Parameter(description = "초대 코드", example = "da2316d7") String inviteCode,
            @Parameter(description = "계약 UUID", example = "36865103-5d08-4139-ba4a-b32da2316d7f") String contractUuid,
            @Parameter(hidden = true) CustomJwtPrincipal principal);
}