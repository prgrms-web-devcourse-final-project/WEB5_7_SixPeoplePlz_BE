package me.jinjjahalgae.domain.proof.dto.response;

import me.jinjjahalgae.domain.proof.enums.ProofStatus;

/**
 * 인증 달력 리스트 응답을 위한 base response
 * 감독자와 계약자의 요구하는 형태가 약간 달라 겹치는 필드를 하나로 관리
 */
public record ProofSimpleResponse(
        String imageKey,            // 1 번째 이미지 (대표 사진)
        ProofStatus status,         // 인증 상태 (승인/거절)
        int totalSupervisors,       // 총 감독자 수
        int completeSupervisors,    // 참여한 감독자 수 (승인/거절 처리를 완료한)
        long proofId                // 인증 id
) {
}
