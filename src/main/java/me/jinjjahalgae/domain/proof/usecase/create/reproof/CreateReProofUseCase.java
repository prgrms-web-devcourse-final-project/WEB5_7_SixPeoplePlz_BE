package me.jinjjahalgae.domain.proof.usecase.create.reproof;

import me.jinjjahalgae.domain.proof.usecase.create.common.ProofCreateRequest;

public interface CreateReProofUseCase {

    void execute(ProofCreateRequest request, Long proofId, Long userId);
}
