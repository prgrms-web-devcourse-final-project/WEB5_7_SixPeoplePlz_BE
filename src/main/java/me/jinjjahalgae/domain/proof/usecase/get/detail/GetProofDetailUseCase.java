package me.jinjjahalgae.domain.proof.usecase.get.detail;

import me.jinjjahalgae.domain.proof.usecase.get.detail.dto.ProofDetailResponse;

public interface GetProofDetailUseCase {

    ProofDetailResponse execute(Long proofId, Long userId);
}
