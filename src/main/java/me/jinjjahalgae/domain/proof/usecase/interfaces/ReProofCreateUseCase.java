package me.jinjjahalgae.domain.proof.usecase.interfaces;

import me.jinjjahalgae.domain.proof.dto.request.ProofCreateRequest;

public interface ReProofCreateUseCase {

    void execute(ProofCreateRequest request, Long proofId);
}
