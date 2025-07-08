package me.jinjjahalgae.domain.invite.usecase.interfaces;

import me.jinjjahalgae.domain.common.UseCase;
import me.jinjjahalgae.domain.invite.dto.response.InviteLinkResponse;

public interface CreateInviteLinkUseCase extends UseCase<Integer, InviteLinkResponse> {
    @Override
    InviteLinkResponse execute(Integer contractId);
}
