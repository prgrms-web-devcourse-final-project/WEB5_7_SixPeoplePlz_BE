package me.jinjjahalgae.domain.proof.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * 인증 생성 request
 * 재인증도 동일하게 사용
 */
public record ProofCreateRequest(
        @NotBlank String firstImageKey,     // 1 번째 이미지 (대표 이미지) -> 최소 1장의 이미지는 필수
        String secondImageKey,              // 2 번째 이미지 (선택)
        String thirdImageKey,               // 3 번째 이미지 (선택)
        String comment                      // 코멘트 (선택)
) {
}
