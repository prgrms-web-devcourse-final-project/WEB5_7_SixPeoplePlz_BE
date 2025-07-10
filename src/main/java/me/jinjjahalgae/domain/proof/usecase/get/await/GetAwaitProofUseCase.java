package me.jinjjahalgae.domain.proof.usecase.get.await;

import java.util.List;

public interface GetAwaitProofUseCase {

    List<ProofAwaitResponse> execute(Long contractId, Long userId);
}
