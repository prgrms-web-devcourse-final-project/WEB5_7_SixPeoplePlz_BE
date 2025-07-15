package me.jinjjahalgae.domain.proof.usecase.getlist.contractorlist.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import me.jinjjahalgae.domain.proof.usecase.getlist.common.ProofSimpleResponse;

import java.time.Instant;
/**
 * 계약자 달력 표시를 위한 response
 * 재인증 필드는 재인증이 존재하지 않는 경우 null
 */
@Schema(
        title = "계약자용 상세 페이지 인증 목록 조회 (달력)",
        description = "계약자 한 달치 인증 목록 응답 DTO",
        example = """
        {
          "date": "2025-07-09T00:34:38Z",
          "endDate": "2025-07-31T00:34:38Z"
          "originalProof": {
            "imageKey": "1234abcd-5678-efgh-ijkl-9012mnopqrst.jpg",
            "status": "APPROVED",
            "totalSupervisors": 4,
            "completedSupervisors": 2,
            "proofId": 20
          },
          "rejectedAt": "2025-07-09T00:34:38Z",
          "reProof": null
        }
        """
)
public record ContractorProofListResponse(
        @Schema(description = "인증이 생성된 날짜 (달력 일 단위 매핑용)")
        Instant date,

        @Schema(description = "계약의 종료일 (종료 2일 전 재인증 요청 불가 검증에 사용할 필드)")
        Instant endDate,

        @Schema(description = "원본 인증 데이터")
        ProofSimpleResponse originalProof,

        @Schema(description = "인증이 거절처리된 시간 (재인증 요청 버튼 출력용)")
        Instant rejectedAt,

        @Schema(description = "재인증 데이터 (해당일에 재인증이 존재하지 않으면 null)")
        ProofSimpleResponse reProof
) {}
