package me.jinjjahalgae.domain.proof.usecase.getcontractorprooflist;

import io.swagger.v3.oas.annotations.media.Schema;
import me.jinjjahalgae.domain.proof.enums.ProofStatus;

/**
 * 인증 달력 리스트 응답을 위한 base response
 * 감독자와 계약자의 요구하는 형태가 약간 달라 겹치는 필드를 하나로 관리
 */
@Schema(
        title = "인증 목록 조회 (달력) 인증/재인증 공통 응답 ",
        description = "인증과 재인증의 공통 필드를 정의한 dto",
        example = """
        {
          "imageKey": "1234abcd-5678-efgh-ijkl-9012mnopqrst.jpg",
          "status": "APPROVED",
          "totalSupervisors": 4,
          "completedSupervisors": 2,
          "proofId": 20
        }
        """
)
public record ProofSimpleResponse(
        @Schema(description = "1 번째 이미지 (대표 사진) 키")
        String imageKey,

        @Schema(description = "인증 상태 (APPROVE_PENDING: 승인대기중, APPROVED: 승인완료, REJECTED: 거절됨")
        ProofStatus status,

        @Schema(description = "계약에 참여한 총 감독자 수")
        int totalSupervisors,

        @Schema(description = "인증에 대한 처리(승인/거절)을 수행한 감독자 수")
        int completeSupervisors,

        @Schema(description = "인증 id")
        long proofId
) {
}
