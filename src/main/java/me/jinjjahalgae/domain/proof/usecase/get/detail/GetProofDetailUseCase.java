package me.jinjjahalgae.domain.proof.usecase.get.detail;

public interface GetProofDetailUseCase {

    ProofDetailResponse execute(Long proofId, Long userId);
}
