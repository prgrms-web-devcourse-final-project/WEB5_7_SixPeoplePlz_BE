package me.jinjjahalgae.domain.proof.usecase.getproofdetail;

public interface GetProofDetailUseCase {

    ProofDetailResponse execute(Long proofId, Long userId);
}
