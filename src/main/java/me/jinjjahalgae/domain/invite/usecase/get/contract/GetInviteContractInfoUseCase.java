package me.jinjjahalgae.domain.invite.usecase.get.contract;

import me.jinjjahalgae.domain.invite.usecase.get.contract.dto.InviteContractInfoResponse;
import me.jinjjahalgae.domain.user.User;

public interface GetInviteContractInfoUseCase {
    InviteContractInfoResponse execute(String contractUuid, User user);
}
