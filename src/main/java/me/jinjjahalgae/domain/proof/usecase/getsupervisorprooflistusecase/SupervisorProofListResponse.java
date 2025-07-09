package me.jinjjahalgae.domain.proof.usecase.getsupervisorprooflistusecase;

import io.swagger.v3.oas.annotations.media.Schema;
import me.jinjjahalgae.domain.feedback.enums.FeedbackStatus;
import me.jinjjahalgae.domain.proof.usecase.getcontractorprooflistusecase.ProofSimpleResponse;

/**
 * 감독자 달력 표시를 위한 response
 * 재인증 필드는 재인증이 존재하지 않는 경우 null
 */
@Schema(
        title = "감독자용 상세 페이지 인증 목록 조회 (달력)",
        description = "감독자 한 달치 인증 목록 응답 DTO",
        example = """
        {
          "date": "2025-07-09",
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
          "reProofFeedbackStatus": "APPROVED",
        }
        """
)
public record SupervisorProofListResponse(
        @Schema(description = "인증이 생성된 날짜 (달력 일 단위 매핑용 yyyy-MM-dd)")
        String date,

        @Schema(description = "원본 인증 데이터")
        ProofSimpleResponse originalProof,

        @Schema(description = "원본 인증에 대한 감독자 자신의 승인 상태(APPROVED: 승인, REJECTED: 거절)")
        FeedbackStatus originalFeedbackStatus,

        @Schema(description = "재인증 데이터 (해당일에 재인증이 존재하지 않으면 null)")
        ProofSimpleResponse reProof,

        @Schema(description = "재인증에 대한 감독자 자신의 승인 상태(APPROVED: 승인, REJECTED: 거절)")
        FeedbackStatus reProofFeedbackStatus
) {
}
