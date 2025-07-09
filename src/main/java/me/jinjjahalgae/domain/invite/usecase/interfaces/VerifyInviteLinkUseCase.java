package me.jinjjahalgae.domain.invite.usecase.interfaces;

public interface VerifyInviteLinkUseCase {
    Boolean execute(String inviteCode);
}
