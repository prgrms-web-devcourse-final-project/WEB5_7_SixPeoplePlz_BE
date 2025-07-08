package me.jinjjahalgae.domain.proof.dto.response;

import me.jinjjahalgae.domain.proof.enums.ProofStatus;

import java.time.LocalDateTime;

/**
 * 대기중인 인증 조회를 위한 response
 */
public record ProofAwaitResponse(
        String titleImageKey,       // 1 번째 이미지 (대표 사진)
        String comment,             // 코멘트 (없으면 null)
        LocalDateTime createdAt,    // 인증 생성 시간 -> HH남음, mm남음 계산용?
        ProofStatus status,         // 대기중 상태
        boolean reProof,            // 원본 인증인지 재인증인지 여부, 재인증 = true 원본 = true
        long proofId                // 인증 id
) {}
