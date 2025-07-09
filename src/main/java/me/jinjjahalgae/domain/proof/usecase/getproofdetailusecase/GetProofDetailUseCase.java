package me.jinjjahalgae.domain.proof.usecase.getproofdetailusecase;

public interface GetProofDetailUseCase {

    ProofDetailResponse execute(Long proofId, Long userId);
}
