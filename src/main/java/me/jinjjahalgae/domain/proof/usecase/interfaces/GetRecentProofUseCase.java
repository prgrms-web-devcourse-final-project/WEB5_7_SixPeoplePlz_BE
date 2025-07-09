package me.jinjjahalgae.domain.proof.usecase.interfaces;

import me.jinjjahalgae.domain.proof.dto.response.ProofRecentResponse;

import java.util.List;

public interface GetRecentProofUseCase {
    List<ProofRecentResponse> execute(Long contractId);
}
