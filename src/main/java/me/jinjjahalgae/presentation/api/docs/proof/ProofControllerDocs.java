package me.jinjjahalgae.presentation.api.docs.proof;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.jinjjahalgae.domain.proof.usecase.create.proof.ProofCreateRequest;
import me.jinjjahalgae.domain.proof.usecase.get.await.ProofAwaitResponse;
import me.jinjjahalgae.domain.proof.usecase.getlist.contractorlist.ContractorProofListResponse;
import me.jinjjahalgae.domain.proof.usecase.get.detail.ProofDetailResponse;
import me.jinjjahalgae.domain.proof.usecase.get.recent.ProofRecentResponse;
import me.jinjjahalgae.domain.proof.usecase.getlist.supervisorlist.SupervisorProofListResponse;
import me.jinjjahalgae.global.common.CommonResponse;
import me.jinjjahalgae.global.security.jwt.CustomJwtPrincipal;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(name = "인증 API", description = "계약 인증 관련 API")
public interface ProofControllerDocs {

    @Operation(
            summary = "인증 생성",
            description = "요청한 데이터로 인증 객체를 생성하여 저장"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "인증 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateProofSwaggerResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                                    {
                                      "success": true,
                                      "result": null
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청(해당 일에 인증이 이미 존재하거나 인증 이미지가 존재하지 않는 경우)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "이미 인증이 존재하는 경우",
                                            value = """
                                            {
                                              "success": false,
                                              "code": "PROOF_ALREADY_EXISTS",
                                              "message": "해당 날짜에 이미 인증이 존재합니다."
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "인증 사진이 1장도 존재하지 않는 경우",
                                            value = """
                                            {
                                              "success": false,
                                              "code": "IMAGE_REQUIRED",
                                              "message": "최소 1장 이상의 이미지가 필요합니다."
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
                                            name = "인증 실패",
                                            value = """
                                            {
                                              "success": false,
                                              "code": "INVALID_TOKEN",
                                              "message": "유효하지 않은 토큰입니다."
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "만료된 토큰",
                                            value = """
                                            {
                                              "success": false,
                                              "code": "EXPIRED_TOKEN",
                                              "message": "토큰이 만료되었습니다."
                                            }
                                            """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "계약에 대한 인증 생성 권한이 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "자신의 계약이 아닌 경우",
                                    value = """
                                    {
                                      "success": false,
                                      "code": "ACCESS_DENIED",
                                      "message": "계약에 대한 접근 권한이 없습니다."
                                    }
                                    """
                                    )


                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "인증과 연결된 계약이 존재하지 않는 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "원본 인증에 대한 계약이 존재하지 않는 경우",
                                    value = """
                                    {
                                      "success": false,
                                      "code": "CONTRACT_NOT_FOUND",
                                      "message": "존재하지 않는 계약입니다."
                                    }
                                    """
                            )

                    )

            )
    })
    CommonResponse<Void> createProof(
            @Parameter(description = "인증 등록 정보", required = true) ProofCreateRequest req,
            @Parameter(description = "계약 id", required = true) @PathVariable Long contractId,
            @Parameter(hidden = true) CustomJwtPrincipal user
    );




    @Operation(
            summary = "재인증 생성",
            description = "요청한 데이터로 재인증 객체를 생성하여 저장"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "재인증 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateReProofSwaggerResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                                    {
                                      "success": true,
                                      "result": null
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청(해당 일에 재인증이 이미 존재하거나 재인증 이미지가 존재하지 않는 경우)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "이미 재인증이 존재하는 경우",
                                            value = """
                                            {
                                              "success": false,
                                              "code": "REPROOF_ALREADY_EXISTS",
                                              "message": "해당 날짜에 이미 재인증이 존재합니다."
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "재인증 사진이 1장도 존재하지 않는 경우",
                                            value = """
                                            {
                                              "success": false,
                                              "code": "IMAGE_REQUIRED",
                                              "message": "최소 1장 이상의 이미지가 필요합니다."
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
                                            name = "인증 실패",
                                            value = """
                                            {
                                              "success": false,
                                              "code": "INVALID_TOKEN",
                                              "message": "유효하지 않은 토큰입니다."
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "만료된 토큰",
                                            value = """
                                            {
                                              "success": false,
                                              "code": "EXPIRED_TOKEN",
                                              "message": "토큰이 만료되었습니다."
                                            }
                                            """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "계약에 대한 인증 생성 권한이 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "자신의 계약이 아닌 경우",
                                    value = """
                                    {
                                      "success": false,
                                      "code": "ACCESS_DENIED",
                                      "message": "계약에 대한 접근 권한이 없습니다."
                                    }
                                    """
                            )


                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "원본 인증이 존재하지 않거나 원본 인증과 연결된 계약이 존재하지 않는 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "원본 인증이 존재하지 않는 경우",
                                            value = """
                                            {
                                              "success": false,
                                              "code": "PROOF_NOT_FOUND",
                                              "message": "해당 인증을 찾을 수 없습니다."
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "원본 인증에 대한 계약이 존재하지 않는 경우",
                                            value = """
                                            {
                                              "success": false,
                                              "code": "CONTRACT_NOT_FOUND",
                                              "message": "존재하지 않는 계약입니다."
                                            }
                                            """
                                    )
                            }
                    )

            )
    })
    CommonResponse<Void> createReProof(
            @Parameter(description = "재인증 등록 정보", required = true) ProofCreateRequest req,
            @Parameter(description = "원본 인증 id", required = true) @PathVariable Long proofId,
            @Parameter(hidden = true) CustomJwtPrincipal user
    );


    @Operation(
            summary = "대기중인 인증들 조회",
            description = "승인/거절 처리를 해야하는 대기중인 인증 데이터를 응답에 담아 반환 (대기중인 인증이 여러 개일 수 있음)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "대기중인 인증 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GetAwaitProofsSwaggerResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value =
                                    """
                                    {
                                      "success": true,
                                      "result": [
                                        {
                                          "titleImageKey": "1234abcd-5678-efgh-ijkl-9012mnopqrst.jpg",
                                          "comment": "6시에 헬스장 가서 7시30분까지 운동했습니다.",
                                          "createdAt": "2025-07-09T19:30:00+09:00",
                                          "status": "APPROVE_PENDING",
                                          "reProof": false,
                                          "proofId": 11
                                        },
                                        {
                                          "titleImageKey": "3453abcd-5678-efgh-ijkl-9012mnopqrst.jpg",
                                          "comment": "6시에 헬스장 가서 7시30분까지 운동했습니다.",
                                          "createdAt": "2025-07-08T19:30:00+09:00",
                                          "status": "APPROVE_PENDING",
                                          "reProof": false,
                                          "proofId": 10
                                        }
                                      ]
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
                            examples = {
                                    @ExampleObject(
                                            name = "인증 실패",
                                            value = """
                                            {
                                              "success": false,
                                              "code": "INVALID_TOKEN",
                                              "message": "유효하지 않은 토큰입니다."
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "만료된 토큰",
                                            value = """
                                            {
                                              "success": false,
                                              "code": "EXPIRED_TOKEN",
                                              "message": "토큰이 만료되었습니다."
                                            }
                                            """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "계약에 대한 접근 권한이 없음 ",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "감독중인 계약이 아닌 경우",
                                    value = """
                                    {
                                      "success": false,
                                      "code": "ACCESS_DENIED",
                                      "message": "계약에 대한 접근 권한이 없습니다."
                                    }
                                    """
                            )


                    )
            )
    })
    CommonResponse<List<ProofAwaitResponse>> getAwaitProofs(
            @Parameter(description = "계약 id", required = true) @PathVariable Long contractId,
            @Parameter(hidden = true) CustomJwtPrincipal user
    );


    @Operation(
            summary = "최근 인증 조회",
            description = "최근 3개의 인증의 데이터를 응답에 담아 반환 (0~3개 응답 예정)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "최근 인증 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GetRecentProofsSwaggerResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value =
                                    """
                                    {
                                      "success": true,
                                      "result": [
                                          {
                                            "imageKey": "1234abcd-5678-efgh-ijkl-9012mnopqrst.jpg",
                                            "comment": "6시에 헬스장 가서 7시30분까지 운동했습니다.",
                                            "createdAt": "2025-07-09T19:30:00+09:00",
                                            "status": "APPROVE_PENDING",
                                            "reProof": false,
                                            "proofId": 12
                                          },
                                          {
                                            "imageKey": "3453abcd-5678-efgh-ijkl-9012mnopqrst.jpg",
                                            "comment": "6시에 헬스장 가서 7시30분까지 운동했습니다.",
                                            "createdAt": "2025-07-08T19:30:00+09:00",
                                            "status": "APPROVED",
                                            "reProof": false,
                                            "proofId": 11
                                          },
                                          {
                                            "imageKey": "3453abcd-5678-efgh-ijkl-9012mnopqrst.jpg",
                                            "comment": "6시에 헬스장 가서 7시30분까지 운동했습니다.",
                                            "createdAt": "2025-07-07T19:30:00+09:00",
                                            "status": "APPROVED",
                                            "reProof": false,
                                            "proofId": 10
                                          }
                                       ]
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
                            examples = {
                                    @ExampleObject(
                                            name = "인증 실패",
                                            value = """
                                            {
                                              "success": false,
                                              "code": "INVALID_TOKEN",
                                              "message": "유효하지 않은 토큰입니다."
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "만료된 토큰",
                                            value = """
                                            {
                                              "success": false,
                                              "code": "EXPIRED_TOKEN",
                                              "message": "토큰이 만료되었습니다."
                                            }
                                            """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "계약에 대한 접근 권한이 없음 ",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "자신의 계약이 아닌 경우",
                                    value = """
                                    {
                                      "success": false,
                                      "code": "ACCESS_DENIED",
                                      "message": "계약에 대한 접근 권한이 없습니다."
                                    }
                                    """
                            )


                    )
            )
    })
    CommonResponse<List<ProofRecentResponse>> getRecentProofs(
            @Parameter(description = "계약 id", required = true) @PathVariable Long contractId,
            @Parameter(hidden = true) CustomJwtPrincipal user
    );


    @Operation(
            summary = "인증 상세 조회",
            description = "인증에 대한 상세 정보를 응답에 담아 반환"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "인증 상세 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GetProofDetailSwaggerResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value =
                                    """
                                    {
                                      "success": true,
                                      "result": {
                                        "imageKeys": [
                                          "1234abcd-5678-efgh-ijkl-9012mnopqrst.jpg",
                                          "5678ssss-5678-efgh-ijkl-9012mnopqrst.jpg",
                                          "1468qwer-5678-efgh-ijkl-9012mnopqrst.jpg"
                                        ],
                                        "comment": "6시에 헬스장 가서 7시30분까지 운동했습니다.",
                                        "status": "REJECTED",
                                        "createdAt": "2025-07-09T13:30:00",
                                        "reProof": false,
                                        "feedbacks": [
                                          {
                                            "createdAt": "2025-07-09T13:30:00",
                                            "status": "APPROVED",
                                            "comment": "확인했습니다!"
                                          },
                                          {
                                            "createdAt": "2025-07-09T14:55:00",
                                            "status": "REJECTED",
                                            "comment": "사진만 찍고 온거 아님?"
                                          },
                                          {
                                            "createdAt": "2025-07-09T16:00:00",
                                            "status": "REJECTED",
                                            "comment": null
                                          }
                                        ],
                                        "proofId": 17
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
                            examples = {
                                    @ExampleObject(
                                            name = "인증 실패",
                                            value = """
                                            {
                                              "success": false,
                                              "code": "INVALID_TOKEN",
                                              "message": "유효하지 않은 토큰입니다."
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "만료된 토큰",
                                            value = """
                                            {
                                              "success": false,
                                              "code": "EXPIRED_TOKEN",
                                              "message": "토큰이 만료되었습니다."
                                            }
                                            """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "계약에 대한 접근 권한이 없음 ",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "자신의 계약이 아니거나 감독중인 계약이 아닌 경우",
                                    value = """
                                    {
                                      "success": false,
                                      "code": "ACCESS_DENIED",
                                      "message": "계약에 대한 접근 권한이 없습니다."
                                    }
                                    """
                            )


                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "인증이 존재하지 않는 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "인증이 존재하지 않는 경우",
                                    value =
                                    """
                                    {
                                      "success": false,
                                      "code": "PROOF_NOT_FOUND",
                                      "message": "해당 인증을 찾을 수 없습니다."
                                    }
                                    """
                            )

                    )

            )
    })
    CommonResponse<ProofDetailResponse> getProofDetail(
            @Parameter(description = "인증 id", required = true) @PathVariable Long proofId,
            @Parameter(hidden = true) CustomJwtPrincipal user
    );

    @Operation(
            summary = "계약자용 인증 목록 조회",
            description = "한 달에 대한 인증 정보를 응답에 담아 반환"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "계약자용 인증 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GetContractorProofListSwaggerResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value =
                                    """
                                    {
                                      "success": true,
                                      "result": [
                                        {
                                          "date": "2025-07-01",
                                          "originalProof": {
                                            "imageKey": "1234abcd-5678-efgh-ijkl-9012mnopqrst.jpg",
                                            "status": "REJECTED",
                                            "totalSupervisors": 4,
                                            "completedSupervisors": 2,
                                            "proofId": 20
                                          },
                                          "rejectedAt": "2025-07-01T10:30:00+09:00",
                                          "reProof": {
                                            "imageKey": "5678abcd-5678-efgh-ijkl-9012mnopqrst.jpg",
                                            "status": "APPROVED",
                                            "totalSupervisors": 4,
                                            "completedSupervisors": 2,
                                            "proofId": 21
                                          }
                                        },
                                        {
                                          "date": "2025-07-02",
                                          "originalProof": {
                                            "imageKey": "2344abcd-5678-efgh-ijkl-9012mnopqrst.jpg",
                                            "status": "APPROVED",
                                            "totalSupervisors": 4,
                                            "completedSupervisors": 2,
                                            "proofId": 24
                                          },
                                          "rejectedAt": "2025-07-02T11:30:00+09:00",
                                          "reProof": null
                                        }
                                      ]
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 값으로 요청",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "년, 월 값이 잘못된 경우",
                                    value =
                                    """
                                    {
                                      "success": false,
                                      "code": "INVALID_YEAR_MONTH",
                                      "message": "유효하지 않은 년, 월입니다."
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
                            examples = {
                                    @ExampleObject(
                                            name = "인증 실패",
                                            value = """
                                            {
                                              "success": false,
                                              "code": "INVALID_TOKEN",
                                              "message": "유효하지 않은 토큰입니다."
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "만료된 토큰",
                                            value = """
                                            {
                                              "success": false,
                                              "code": "EXPIRED_TOKEN",
                                              "message": "토큰이 만료되었습니다."
                                            }
                                            """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "계약에 대한 접근 권한이 없음 ",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "자신의 계약이 아닌 경우",
                                    value = """
                                    {
                                      "success": false,
                                      "code": "ACCESS_DENIED",
                                      "message": "계약에 대한 접근 권한이 없습니다."
                                    }
                                    """
                            )


                    )
            )
    })
    CommonResponse<List<ContractorProofListResponse>> getContractorProofList(
            @Parameter(description = "계약 id", required = true) @PathVariable Long contractId,
            @Parameter(description = "년", required = true) int year,
            @Parameter(description = "월", required = true) int month,
            @Parameter(hidden = true) CustomJwtPrincipal user
    );

    @Operation(
            summary = "감독자용 인증 목록 조회",
            description = "한 달에 대한 처리 완료된 인증 정보를 응답에 담아 반환 (대기중인 인증은 반환 X)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "감독자용 인증 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GetSupervisorProofListSwaggerResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value =
                                    """
                                    {
                                      "success": true,
                                      "result": [
                                        {
                                          "date": "2025-07-01",
                                          "originalProof": {
                                            "imageKey": "1234abcd-5678-efgh-ijkl-9012mnopqrst.jpg",
                                            "status": "REJECTED",
                                            "totalSupervisors": 4,
                                            "completedSupervisors": 2,
                                            "proofId": 20
                                          },
                                          "originalFeedbackStatus": "REJECTED",
                                          "reProof": {
                                            "imageKey": "5678abcd-5678-efgh-ijkl-9012mnopqrst.jpg",
                                            "status": "APPROVED",
                                            "totalSupervisors": 4,
                                            "completedSupervisors": 2,
                                            "proofId": 21
                                          },
                                          "reProofFeedbackStatus": "APPROVED"
                                        },
                                        {
                                          "date": "2025-07-02",
                                          "originalProof": {
                                            "imageKey": "2344abcd-5678-efgh-ijkl-9012mnopqrst.jpg",
                                            "status": "APPROVED",
                                            "totalSupervisors": 4,
                                            "completedSupervisors": 2,
                                            "proofId": 24
                                          },
                                          "originalFeedbackStatus": "REJECTED",
                                          "reProof": null,
                                          "reProofFeedbackStatus": null
                                        }
                                      ]
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 값으로 요청",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "년, 월 값이 잘못된 경우",
                                    value =
                                    """
                                    {
                                      "success": false,
                                      "code": "INVALID_YEAR_MONTH",
                                      "message": "유효하지 않은 년, 월입니다."
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
                            examples = {
                                    @ExampleObject(
                                            name = "인증 실패",
                                            value = """
                                            {
                                              "success": false,
                                              "code": "INVALID_TOKEN",
                                              "message": "유효하지 않은 토큰입니다."
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "만료된 토큰",
                                            value = """
                                            {
                                              "success": false,
                                              "code": "EXPIRED_TOKEN",
                                              "message": "토큰이 만료되었습니다."
                                            }
                                            """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "계약에 대한 접근 권한이 없음 ",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "감독중인 계약이 아닌 경우",
                                    value = """
                                    {
                                      "success": false,
                                      "code": "ACCESS_DENIED",
                                      "message": "계약에 대한 접근 권한이 없습니다."
                                    }
                                    """
                            )


                    )
            )
    })
    CommonResponse<List<SupervisorProofListResponse>> getSupervisorProofList(
            @Parameter(description = "계약 id", required = true) @PathVariable Long contractId,
            @Parameter(description = "년", required = true) int year,
            @Parameter(description = "월", required = true) int month,
            @Parameter(hidden = true) CustomJwtPrincipal user
    );
}
