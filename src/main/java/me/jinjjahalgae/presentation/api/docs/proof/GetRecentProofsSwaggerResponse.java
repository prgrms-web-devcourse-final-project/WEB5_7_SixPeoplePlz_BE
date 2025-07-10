package me.jinjjahalgae.presentation.api.docs.proof;

import me.jinjjahalgae.domain.proof.dto.response.ProofRecentResponse;
import me.jinjjahalgae.global.common.CommonResponse;

public class GetRecentProofsSwaggerResponse extends CommonResponse<ProofRecentResponse> {
    public GetRecentProofsSwaggerResponse(ProofRecentResponse result) {
        super(result);
    }
}
