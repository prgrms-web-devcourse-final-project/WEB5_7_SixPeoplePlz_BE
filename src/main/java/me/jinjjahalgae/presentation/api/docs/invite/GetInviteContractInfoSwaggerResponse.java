package me.jinjjahalgae.presentation.api.docs.invite;

import me.jinjjahalgae.domain.invite.usecase.get.contract.dto.InviteContractInfoResponse;
import me.jinjjahalgae.global.common.CommonResponse;

public class GetInviteContractInfoSwaggerResponse extends CommonResponse<InviteContractInfoResponse> {
    public GetInviteContractInfoSwaggerResponse(InviteContractInfoResponse result) {
        super(result);
    }
}