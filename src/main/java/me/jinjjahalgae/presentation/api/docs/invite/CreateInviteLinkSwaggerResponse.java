package me.jinjjahalgae.presentation.api.docs.invite;

import me.jinjjahalgae.domain.invite.usecase.create.invite.dto.InviteLinkResponse;
import me.jinjjahalgae.global.common.CommonResponse;

public class CreateInviteLinkSwaggerResponse extends CommonResponse<InviteLinkResponse> {
    public CreateInviteLinkSwaggerResponse(InviteLinkResponse result) {
        super(result);
    }
}