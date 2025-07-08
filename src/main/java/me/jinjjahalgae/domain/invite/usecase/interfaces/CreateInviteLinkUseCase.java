package me.jinjjahalgae.domain.invite.usecase.interfaces;

import me.jinjjahalgae.domain.common.UseCase;
import me.jinjjahalgae.domain.invite.dto.request.CreateInviteLinkRequest;
import me.jinjjahalgae.domain.invite.dto.response.CreateInviteLinkResponse;

public interface CreateInviteLinkUseCase extends UseCase<CreateInviteLinkRequest, CreateInviteLinkResponse> {
    @Override
    CreateInviteLinkResponse execute(CreateInviteLinkRequest request);
}
