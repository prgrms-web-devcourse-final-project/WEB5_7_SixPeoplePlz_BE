package me.jinjjahalgae.presentation.api.docs.invite;

import me.jinjjahalgae.domain.invite.dto.response.InviteContractInfoResponse;
import me.jinjjahalgae.global.common.CommonResponse;

public class GetInviteContractInfoSwaggerResponse extends CommonResponse<InviteContractInfoResponse> {
    public GetInviteContractInfoSwaggerResponse(InviteContractInfoResponse result) {
        super(result);
    }
}