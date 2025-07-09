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
import me.jinjjahalgae.domain.invite.dto.request.InviteLinkVerifyRequest;
import me.jinjjahalgae.domain.invite.dto.response.ContractUuidResponse;
import me.jinjjahalgae.domain.invite.dto.response.InviteContractInfoResponse;
import me.jinjjahalgae.domain.invite.dto.response.InviteLinkResponse;
import me.jinjjahalgae.global.common.CommonResponse;
import me.jinjjahalgae.global.exception.ErrorResponse;
import me.jinjjahalgae.global.security.jwt.CustomJwtPrincipal;
import me.jinjjahalgae.presentation.api.docs.NoContentSwaggerResponse;

@Tag(name = "초대 API", description = "초대 링크 및 계약서 조회 관련 API")
public interface InviteControllerDocs {

    @Operation(summary = "초대링크 생성", description = "특정 계약에 대한 초대링크와 임시 비밀번호를 생성합니다. 인증이 필요합니다.",
            security = { @SecurityRequirement(name = "bearerAuth") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "초대링크 생성 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateInviteLinkSwaggerResponse.class),
                            examples = @ExampleObject(name = "초대링크 생성 성공", value = """
                                 {
                                   "success": true,
                                   "result": {
                                     "inviteUrl": "https://jinjjahalgae.xyz/api/invite/da2316d7",
                                     "password": "ac08ee16"
                                   }
                                 }"""))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 계약",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    CommonResponse<InviteLinkResponse> createInviteLink(
            @Parameter(description = "계약 ID", example = "1") Long contractId);


    @Operation(summary = "초대링크 유효성 검사", description = "초대 코드가 유효한지(만료되지 않았는지) 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "초대링크 유효함",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = NoContentSwaggerResponse.class),
                            examples = @ExampleObject(name = "성공 응답", value = """
                                 { "success": true, "result": null }
                                 """))),
            @ApiResponse(responseCode = "404", description = "존재하지 않거나 만료된 초대링크",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    CommonResponse<Void> checkInviteLink(
            @Parameter(description = "초대 코드", example = "da2316d7") String inviteCode);


    @Operation(summary = "초대링크 비밀번호 확인", description = "입력된 비밀번호가 유효한지 검증하고, 성공 시 계약 UUID를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 검증 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VerifyPasswordSwaggerResponse.class),
                            examples = @ExampleObject(name = "비밀번호 검증 성공", value = """
                                 {
                                   "success": true,
                                   "result": {
                                     "contractUuid": "36865103-5d08-4139-ba4a-b32da2316d7f"
                                   }
                                 }"""))),
            @ApiResponse(responseCode = "401", description = "비밀번호 불일치",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않거나 만료된 초대링크",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    CommonResponse<ContractUuidResponse> verifyPassword(
            @Parameter(description = "초대 코드", example = "da2316d7") String inviteCode,
            @Valid InviteLinkVerifyRequest request);


    @Operation(summary = "초대 계약서 상세 조회", description = "비밀번호 검증 후 계약서의 상세 정보를 조회합니다. 인증이 필요합니다.",
            security = { @SecurityRequirement(name = "bearerAuth") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "계약서 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GetInviteContractInfoSwaggerResponse.class),
                            examples = @ExampleObject(name = "계약서 조회 성공", value = """
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
                                       {
                                         "supervisorName": "김감독",
                                         "supervisorSignatureKey": "1a2b3c4d-5e6f-7g8h-9i0j-1k2l3m4n5o6p"
                                       },
                                       {
                                         "supervisorName": "박감시",
                                         "supervisorSignatureKey": "9z8y7x6w-5v4u-3t2s-1r0q-p9o8n7m6l5k4"
                                       }
                                     ]
                                   }
                                 }"""))),
            @ApiResponse(responseCode = "400", description = "이미 참여한 계약",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "감독자 인원 초과",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    CommonResponse<InviteContractInfoResponse> getContractInfo(
            @Parameter(description = "초대 코드", example = "da2316d7") String inviteCode,
            @Parameter(description = "계약 UUID", example = "36865103-5d08-4139-ba4a-b32da2316d7f") String contractUuid,
            @Parameter(hidden = true) CustomJwtPrincipal principal);
}