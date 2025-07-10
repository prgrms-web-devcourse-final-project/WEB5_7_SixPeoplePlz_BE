package me.jinjjahalgae.domain.proof.usecase.create.proof;

public interface CreateProofUseCase {

    void execute(ProofCreateRequest request, Long contractId, Long userId);
}
