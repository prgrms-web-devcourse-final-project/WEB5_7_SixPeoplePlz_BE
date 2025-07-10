package me.jinjjahalgae.presentation.api.docs.contract;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.jinjjahalgae.domain.contract.usecase.create.dto.CreateContractRequest;
import me.jinjjahalgae.domain.contract.usecase.create.dto.CreateContractResponse;
import me.jinjjahalgae.domain.contract.usecase.get.detail.dto.ContractDetailResponse;
import me.jinjjahalgae.domain.contract.usecase.get.list.dto.ContractListResponse;
import me.jinjjahalgae.domain.contract.usecase.update.dto.ContractUpdateRequest;
import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import me.jinjjahalgae.global.common.CommonResponse;
import me.jinjjahalgae.global.exception.ErrorResponse;
import me.jinjjahalgae.global.security.jwt.CustomJwtPrincipal;
import me.jinjjahalgae.presentation.api.docs.NoContentSwaggerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Tag(name = "계약 API", description = "계약 생성, 조회, 수정 관련 API")
public interface ContractControllerDocs {

    @Operation(
            summary = "계약 생성",
            description = "새로운 계약을 생성합니다. 계약자가 작성한 계약서 정보를 저장하고 감독자 초대를 위한 준비를 합니다.",
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "계약 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateContractSwaggerResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                    {
                      "success": true,
                      "result": {
                        "contractId": 1,
                        "contractUuid": "123e4567-e89b-12d3-a456-426614174000"
                      }
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (필수 필드 누락, 유효성 검증 실패)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "필수 필드 누락",
                                            value = """
                        {
                          "success": false,
                          "code": "BAD_REQUEST",
                          "message": "title은 필수 입력 값입니다."
                        }
                        """
                                    ),
                                    @ExampleObject(
                                            name = "날짜 유효성 실패",
                                            value = """
                        {
                          "success": false,
                          "code": "BAD_REQUEST",
                          "message": "계약 종료일은 시작일보다 늦어야 합니다."
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
            )
    })
    CommonResponse<CreateContractResponse> createContract(
            @Parameter(hidden = true) CustomJwtPrincipal user,
            @Parameter(description = "계약 생성 요청 DTO", required = true) CreateContractRequest request
    );

    @Operation(
            summary = "계약 목록 조회",
            description = "사용자의 계약 목록을 상태별로 페이징하여 조회합니다. " +
                    "메인 페이지용(PENDING, IN_PROGRESS)과 히스토리 페이지용(COMPLETED, FAILED, ABANDONED)으로 구분하여 사용합니다.",
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "계약 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GetContractListSwaggerResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "메인 페이지 응답",
                                            value = """
                        {
                          "success": true,
                          "result": {
                            "content": [
                              {
                                "contractId": 1,
                                "contractUuid": "123e4567-e89b-12d3-a456-426614174000",
                                "title": "매일 운동하기",
                                "contractStatus": "IN_PROGRESS",
                                "proofPerWeek": 7,
                                "startDate": "2024-01-01T09:00:00",
                                "endDate": "2024-01-31T23:59:59",
                                "reward": "치킨 먹기",
                                "penalty": "치킨 못 먹기",
                                "achievementPercent": 65.0,
                                "periodPercent": 45.0
                              },
                              {
                                "contractId": 2,
                                "contractUuid": "456e7890-e89b-12d3-a456-426614174001",
                                "title": "금연하기",
                                "contractStatus": "PENDING",
                                "proofPerWeek": 7,
                                "startDate": "2024-02-01T09:00:00",
                                "endDate": "2024-02-29T23:59:59",
                                "reward": "맛있는 음식 먹기",
                                "penalty": "용돈 줄이기",
                                "achievementPercent": 0.0,
                                "periodPercent": 0.0
                              }
                            ],
                            "pageable": {
                              "sort": {
                                "empty": false,
                                "sorted": true,
                                "unsorted": false
                              },
                              "pageNumber": 0,
                              "pageSize": 10,
                              "offset": 0,
                              "paged": true,
                              "unpaged": false
                            },
                            "totalElements": 2,
                            "totalPages": 1,
                            "last": true,
                            "size": 10,
                            "number": 0,
                            "sort": {
                              "empty": false,
                              "sorted": true,
                              "unsorted": false
                            },
                            "numberOfElements": 2,
                            "first": true,
                            "empty": false
                          }
                        }
                        """
                                    ),
                                    @ExampleObject(
                                            name = "히스토리 페이지 응답",
                                            value = """
                        {
                          "success": true,
                          "result": {
                            "content": [
                              {
                                "contractId": 3,
                                "contractUuid": "789e0123-e89b-12d3-a456-426614174002",
                                "title": "독서하기",
                                "contractStatus": "COMPLETED",
                                "proofPerWeek": 3,
                                "startDate": "2023-12-01T09:00:00",
                                "endDate": "2023-12-31T23:59:59",
                                "reward": "새 책 사기",
                                "penalty": "핸드폰 시간 줄이기",
                                "achievementPercent": 100.0,
                                "periodPercent": 100.0
                              }
                            ],
                            "pageable": {
                              "sort": {
                                "empty": false,
                                "sorted": true,
                                "unsorted": false
                              },
                              "pageNumber": 0,
                              "pageSize": 10,
                              "offset": 0,
                              "paged": true,
                              "unpaged": false
                            },
                            "totalElements": 1,
                            "totalPages": 1,
                            "last": true,
                            "size": 10,
                            "number": 0,
                            "sort": {
                              "empty": false,
                              "sorted": true,
                              "unsorted": false
                            },
                            "numberOfElements": 1,
                            "first": true,
                            "empty": false
                          }
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
            )
    })
    CommonResponse<Page<ContractListResponse>> getContracts(
            @Parameter(hidden = true) CustomJwtPrincipal user,
            @Parameter(
                    description = "조회할 계약 상태 목록. 메인: [PENDING, IN_PROGRESS], 히스토리: [COMPLETED, FAILED, ABANDONED]",
                    required = true,
                    example = "PENDING,IN_PROGRESS"
            ) List<ContractStatus> statuses,
            @Parameter(description = "페이징 정보 (기본: page=0, size=10)", required = false,
                    schema      = @Schema(
                            type    = "object",
                            example = "{\n" +
                                    "  \"page\": 0,\n" +
                                    "  \"size\": 1,\n" +
                                    "  \"sort\": [\n" +
                                    "    \"id\"\n" +
                                    "  ]\n" +
                                    "}"
                    )) Pageable pageable
    );

    @Operation(
            summary = "계약 상세 조회",
            description = "특정 계약의 상세 정보를 조회합니다. 계약 내용과 참여자(감독자) 정보를 포함합니다.",
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "계약 상세 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GetContractDetailSwaggerResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                    {
                      "success": true,
                      "result": {
                        "contractId": 1,
                        "contractUuid": "123e4567-e89b-12d3-a456-426614174000",
                        "title": "매일 운동하기",
                        "goal": "매일 1시간 이상 운동하여 건강한 몸 만들기",
                        "penalty": "치킨 못 먹기",
                        "reward": "치킨 2번 먹기",
                        "type": "BASIC",
                        "startDate": "2024-01-01T09:00:00",
                        "endDate": "2024-01-31T23:59:59",
                        "contractStatus": "IN_PROGRESS",
                        "proofPerWeek": 7,
                        "totalProof": 31,
                        "currentProof": 20,
                        "remainingLife": 2,
                        "achievementPercent": 64.5,
                        "periodPercent": 45.2,
                        "participants": [
                          {
                            "userId": 1,
                            "name": "김계약",
                            "role": "CONTRACTOR",
                            "signatureImageKey": "contractor-signature-123.jpg"
                          },
                          {
                            "userId": 2,
                            "name": "박감독",
                            "role": "SUPERVISOR",
                            "signatureImageKey": "supervisor-signature-456.jpg"
                          },
                          {
                            "userId": 3,
                            "name": "이감독",
                            "role": "SUPERVISOR",
                            "signatureImageKey": "supervisor-signature-789.jpg"
                          }
                        ]
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
                    responseCode = "404",
                    description = "계약을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "계약 없음",
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
    CommonResponse<ContractDetailResponse> getContractDetail(
            @Parameter(hidden = true) CustomJwtPrincipal user,
            @Parameter(description = "조회할 계약 ID", required = true, example = "1") Long contractId
    );

    @Operation(
            summary = "계약 수정",
            description = "기존 계약의 내용을 수정합니다. 감독자가 서명하기 전에만 수정 가능하며, 계약자만 수정할 수 있습니다. " +
                    "날짜나 주간 인증 횟수 변경 시 총 인증 횟수가 자동으로 재계산됩니다.",
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "계약 수정 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = NoContentSwaggerResponse.class),
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
                    description = "잘못된 요청 (필수 필드 누락, 유효성 검증 실패)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "필수 필드 누락",
                                            value = """
                        {
                          "success": false,
                          "code": "BAD_REQUEST",
                          "message": "title은 필수 입력 값입니다."
                        }
                        """
                                    ),
                                    @ExampleObject(
                                            name = "감독자 서명 후 수정 시도",
                                            value = """
                        {
                          "success": false,
                          "code": "CONTRACT_ALREADY_SIGNED",
                          "message": "감독자가 서명한 계약은 수정할 수 없습니다."
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
                    description = "접근 권한 없음 (계약자가 아닌 사용자)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "접근 권한 없음",
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
                    description = "계약을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "계약 없음",
                                    value = """
                    {
                      "success": false,
                      "code": "CONTRACT_NOT_FOUND",
                      "message": "계약을 찾을 수 없습니다."
                    }
                    """
                            )
                    )
            )
    })
    CommonResponse<Void> updateContract(
            @Parameter(hidden = true) CustomJwtPrincipal user,
            @Parameter(description = "수정할 계약 ID", required = true, example = "1") Long contractId,
            @Parameter(description = "계약 수정 요청 DTO", required = true) ContractUpdateRequest request
    );
}