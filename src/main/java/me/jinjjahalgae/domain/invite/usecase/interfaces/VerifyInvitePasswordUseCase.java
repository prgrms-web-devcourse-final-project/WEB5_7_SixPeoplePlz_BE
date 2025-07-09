package me.jinjjahalgae.domain.invite.usecase.interfaces;

import me.jinjjahalgae.domain.invite.dto.request.InviteLinkVerifyRequest;

public interface VerifyInvitePasswordUseCase {
    String execute(String inviteCode, InviteLinkVerifyRequest request);
}
