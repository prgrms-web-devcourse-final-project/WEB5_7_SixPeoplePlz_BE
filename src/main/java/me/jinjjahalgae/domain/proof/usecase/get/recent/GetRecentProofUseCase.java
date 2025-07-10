package me.jinjjahalgae.domain.proof.usecase.get.recent;

import java.util.List;

public interface GetRecentProofUseCase {
    List<ProofRecentResponse> execute(Long contractId, Long userId);
}
