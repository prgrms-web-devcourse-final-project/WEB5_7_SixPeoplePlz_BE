package me.jinjjahalgae.domain.proof.usecase.getawaitproof;

import java.util.List;

public interface GetAwaitProofUseCase {

    List<ProofAwaitResponse> execute(Long contractId, Long userId);
}
