package me.jinjjahalgae.domain.proof.usecase.interfaces;

import me.jinjjahalgae.domain.proof.dto.response.ProofDetailResponse;

public interface GetProofDetailUseCase {

    ProofDetailResponse execute(Long proofId, Long userId);
}
