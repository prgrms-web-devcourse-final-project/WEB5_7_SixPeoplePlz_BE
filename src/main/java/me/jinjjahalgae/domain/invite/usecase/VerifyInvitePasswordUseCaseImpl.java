package me.jinjjahalgae.domain.invite.usecase;

import me.jinjjahalgae.domain.invite.dto.request.InviteLinkVerifyRequest;
import me.jinjjahalgae.domain.invite.usecase.interfaces.VerifyInvitePasswordUseCase;

public class VerifyInvitePasswordUseCaseImpl implements VerifyInvitePasswordUseCase {
    @Override
    public String execute(String inviteCode, InviteLinkVerifyRequest request) {
        return null;
    }
}
