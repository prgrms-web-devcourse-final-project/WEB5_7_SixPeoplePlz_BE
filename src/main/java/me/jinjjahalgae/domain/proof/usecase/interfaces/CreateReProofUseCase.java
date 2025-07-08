package me.jinjjahalgae.domain.proof.usecase.interfaces;

import me.jinjjahalgae.domain.proof.dto.request.ProofCreateRequest;

public interface CreateReProofUseCase {

    void execute(ProofCreateRequest request, Long proofId);
}
