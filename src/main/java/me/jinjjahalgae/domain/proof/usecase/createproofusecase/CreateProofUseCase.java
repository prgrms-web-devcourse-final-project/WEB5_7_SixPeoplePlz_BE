package me.jinjjahalgae.domain.proof.usecase.createproofusecase;

public interface CreateProofUseCase {

    void execute(ProofCreateRequest request, Long contractId, Long userId);
}
