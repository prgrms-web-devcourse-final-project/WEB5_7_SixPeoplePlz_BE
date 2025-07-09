package me.jinjjahalgae.domain.invite.usecase.interfaces;

import me.jinjjahalgae.domain.invite.dto.response.InviteContractInfoResponse;
import me.jinjjahalgae.domain.user.User;

public interface GetInviteContractInfoUseCase {
    InviteContractInfoResponse execute(String contractUuid, User user);
}
