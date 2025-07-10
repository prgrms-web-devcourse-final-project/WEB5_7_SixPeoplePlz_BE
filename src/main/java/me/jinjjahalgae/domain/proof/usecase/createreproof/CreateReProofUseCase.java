package me.jinjjahalgae.domain.proof.usecase.createreproof;

import me.jinjjahalgae.domain.proof.usecase.createproof.ProofCreateRequest;

public interface CreateReProofUseCase {

    void execute(ProofCreateRequest request, Long proofId, Long userId);
}
