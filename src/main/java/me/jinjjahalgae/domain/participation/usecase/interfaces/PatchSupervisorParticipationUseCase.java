package me.jinjjahalgae.domain.participation.usecase.interfaces;

import me.jinjjahalgae.domain.user.User;

public interface PatchSupervisorParticipationUseCase {
    void execute(Long contractId, User user);
}
