package me.jinjjahalgae.domain.proof.dto.response;

import me.jinjjahalgae.domain.proof.enums.ProofStatus;

import java.time.LocalDateTime;

/**
 * 최근 3개의 인증 response
 */
public record ProofRecentResponse(
        String imageKey,            // 1 번째 이미지(대표 사진)
        String comment,             // 코멘트 (없으면 null)
        ProofStatus status,         // 인증 상태(승인/거절)
        LocalDateTime createdAt,    // 몇 일자 인증인지
        boolean reProof,            // 재인증 여부, 재인증 = true 원본 = false
        long proofId
) {
}
