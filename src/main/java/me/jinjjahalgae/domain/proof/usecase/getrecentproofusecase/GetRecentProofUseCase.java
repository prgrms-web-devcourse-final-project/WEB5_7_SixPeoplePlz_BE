package me.jinjjahalgae.domain.proof.usecase.getrecentproofusecase;

import java.util.List;

public interface GetRecentProofUseCase {
    List<ProofRecentResponse> execute(Long contractId, Long userId);
}
