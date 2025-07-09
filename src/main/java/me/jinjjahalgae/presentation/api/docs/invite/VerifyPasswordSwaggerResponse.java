package me.jinjjahalgae.presentation.api.docs.invite;

import me.jinjjahalgae.domain.invite.dto.response.ContractUuidResponse;
import me.jinjjahalgae.global.common.CommonResponse;

public class VerifyPasswordSwaggerResponse extends CommonResponse<ContractUuidResponse> {
    public VerifyPasswordSwaggerResponse(ContractUuidResponse result) {
        super(result);
    }
}