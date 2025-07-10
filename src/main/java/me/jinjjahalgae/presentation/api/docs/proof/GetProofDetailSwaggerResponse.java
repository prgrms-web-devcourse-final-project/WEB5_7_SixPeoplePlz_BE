package me.jinjjahalgae.presentation.api.docs.proof;

import me.jinjjahalgae.domain.proof.dto.response.ProofDetailResponse;
import me.jinjjahalgae.global.common.CommonResponse;

public class GetProofDetailSwaggerResponse extends CommonResponse<ProofDetailResponse> {
    public GetProofDetailSwaggerResponse(ProofDetailResponse result) {
        super(result);
    }
}
