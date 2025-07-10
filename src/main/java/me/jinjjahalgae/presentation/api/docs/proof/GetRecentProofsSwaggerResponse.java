package me.jinjjahalgae.presentation.api.docs.proof;

import me.jinjjahalgae.domain.proof.usecase.get.recent.dto.ProofRecentResponse;
import me.jinjjahalgae.global.common.CommonResponse;

public class GetRecentProofsSwaggerResponse extends CommonResponse<ProofRecentResponse> {
    public GetRecentProofsSwaggerResponse(ProofRecentResponse result) {
        super(result);
    }
}
