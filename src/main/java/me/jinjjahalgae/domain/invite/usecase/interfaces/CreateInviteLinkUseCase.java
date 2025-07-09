package me.jinjjahalgae.domain.invite.usecase.interfaces;

import me.jinjjahalgae.domain.invite.dto.response.InviteLinkResponse;

public interface CreateInviteLinkUseCase {
    InviteLinkResponse execute(Long contractId);
}
