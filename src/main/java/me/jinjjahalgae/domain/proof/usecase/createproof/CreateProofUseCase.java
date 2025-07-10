package me.jinjjahalgae.domain.proof.usecase.createproof;

public interface CreateProofUseCase {

    void execute(ProofCreateRequest request, Long contractId, Long userId);
}
