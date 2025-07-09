package me.jinjjahalgae.domain.proof.usecase.createreproofusecase;

import me.jinjjahalgae.domain.proof.usecase.createproofusecase.ProofCreateRequest;

public interface CreateReProofUseCase {

    void execute(ProofCreateRequest request, Long proofId, Long userId);
}
