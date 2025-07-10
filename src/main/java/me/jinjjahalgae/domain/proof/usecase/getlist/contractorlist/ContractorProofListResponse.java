package me.jinjjahalgae.domain.proof.usecase.getlist.contractorlist;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
/**
 * 계약자 달력 표시를 위한 response
 * 재인증 필드는 재인증이 존재하지 않는 경우 null
 */
@Schema(
        title = "계약자용 상세 페이지 인증 목록 조회 (달력)",
        description = "계약자 한 달치 인증 목록 응답 DTO",
        example = """
        {
          "date": "2025-07-09",
          "originalProof": {
            "imageKey": "1234abcd-5678-efgh-ijkl-9012mnopqrst.jpg",
            "status": "APPROVED",
            "totalSupervisors": 4,
            "completedSupervisors": 2,
            "proofId": 20
          },
          "rejectedAt": "2025-07-09T13:30:00+09:00",
          "reProof": null
        }
        """
)
public record ContractorProofListResponse(
        @Schema(description = "인증이 생성된 날짜 (달력 일 단위 매핑용 yyyy-MM-dd)")
        String date,

        @Schema(description = "원본 인증 데이터")
        ProofSimpleResponse originalProof,

        @Schema(description = "인증이 거절처리된 시간 (재인증 요청 버튼 출력용)")
        LocalDateTime rejectedAt,

        @Schema(description = "재인증 데이터 (해당일에 재인증이 존재하지 않으면 null)")
        ProofSimpleResponse reProof
) {}
