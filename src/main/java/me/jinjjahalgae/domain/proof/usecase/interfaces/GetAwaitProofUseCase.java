package me.jinjjahalgae.domain.proof.usecase.interfaces;

import me.jinjjahalgae.domain.proof.dto.response.ProofAwaitResponse;

import java.util.List;

public interface GetAwaitProofUseCase {

    List<ProofAwaitResponse> execute(Long contractId, Long userId);
}
