package me.jinjjahalgae.domain.proof.usecase.interfaces;

import me.jinjjahalgae.domain.proof.dto.request.ProofCreateRequest;

public interface CreateProofUseCase {

    void execute(ProofCreateRequest request, Long contractId);
}
