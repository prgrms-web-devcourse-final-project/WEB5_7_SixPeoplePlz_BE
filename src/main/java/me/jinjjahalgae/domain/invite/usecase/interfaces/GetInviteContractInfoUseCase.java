package me.jinjjahalgae.domain.invite.usecase.interfaces;

import me.jinjjahalgae.domain.invite.dto.response.InviteContractInfoResponse;

public interface GetInviteContractInfoUseCase {
    InviteContractInfoResponse execute(String inviteCode, String contractUuid);
}
