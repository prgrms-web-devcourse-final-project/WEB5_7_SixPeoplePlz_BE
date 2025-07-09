package me.jinjjahalgae.domain.invite.usecase.interfaces;

import me.jinjjahalgae.domain.invite.dto.request.InviteLinkVerifyRequest;
import me.jinjjahalgae.domain.invite.dto.response.ContractUuidResponse;

public interface VerifyInvitePasswordUseCase {
    ContractUuidResponse execute(String inviteCode, InviteLinkVerifyRequest request);
}
