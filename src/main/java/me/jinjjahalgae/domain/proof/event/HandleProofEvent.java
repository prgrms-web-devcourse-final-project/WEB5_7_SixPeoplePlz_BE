package me.jinjjahalgae.domain.proof.event;

/**
 * 인증 상태를 처리하는 이벤트 파라미터
 * @param proofId - 처리할 인증 id
 */
public record HandleProofEvent(
        Long proofId
) {
}
