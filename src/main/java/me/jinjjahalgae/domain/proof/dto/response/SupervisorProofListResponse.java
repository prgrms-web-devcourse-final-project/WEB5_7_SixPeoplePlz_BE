package me.jinjjahalgae.domain.proof.dto.response;

/**
 * 감독자 달력 표시를 위한 response
 * 재인증 필드는 재인증이 존재하지 않는 경우 null
 */
public record SupervisorProofListResponse(
        String date,   // 달력 라우팅용 일 단위 매핑 (yyyy-MM-dd)

        ProofSimpleResponse originalProof,
//        FeedbackStatus originalFeedbackStatus,   // 감독자 자신의 승인 상태 출력용

        ProofSimpleResponse reProof
//        FeedbackStatus reProofFeedbackStatus      // 감독자 자신의 승인 상태 출력용
) {
}
