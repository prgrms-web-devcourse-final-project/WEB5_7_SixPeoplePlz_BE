package me.jinjjahalgae.presentation.api.docs.proof;

import me.jinjjahalgae.domain.proof.usecase.getproofdetail.ProofDetailResponse;
import me.jinjjahalgae.global.common.CommonResponse;

public class GetProofDetailSwaggerResponse extends CommonResponse<ProofDetailResponse> {
    public GetProofDetailSwaggerResponse(ProofDetailResponse result) {
        super(result);
    }
}
