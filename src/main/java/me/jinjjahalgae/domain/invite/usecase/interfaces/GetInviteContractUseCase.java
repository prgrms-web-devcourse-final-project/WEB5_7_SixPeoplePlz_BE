package me.jinjjahalgae.domain.invite.usecase.interfaces;

import me.jinjjahalgae.domain.invite.dto.response.InviteContractResponse;

public interface GetInviteContractUseCase {
    InviteContractResponse execute(String inviteCode, String contractUuid);
}
