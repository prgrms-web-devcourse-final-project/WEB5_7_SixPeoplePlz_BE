package me.jinjjahalgae.domain.invite.usecase.verify.password;

import me.jinjjahalgae.domain.invite.usecase.verify.password.dto.VerifyInvitePasswordRequest;
import me.jinjjahalgae.domain.invite.usecase.verify.password.dto.ContractUuidResponse;

public interface VerifyInvitePasswordUseCase {
    ContractUuidResponse execute(String inviteCode, VerifyInvitePasswordRequest request);
}
