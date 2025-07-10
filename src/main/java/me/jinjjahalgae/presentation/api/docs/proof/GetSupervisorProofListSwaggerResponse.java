package me.jinjjahalgae.presentation.api.docs.proof;

import me.jinjjahalgae.domain.proof.dto.response.SupervisorProofListResponse;
import me.jinjjahalgae.global.common.CommonResponse;

public class GetSupervisorProofListSwaggerResponse extends CommonResponse<SupervisorProofListResponse> {
    public GetSupervisorProofListSwaggerResponse(SupervisorProofListResponse result) {
        super(result);
    }
}
