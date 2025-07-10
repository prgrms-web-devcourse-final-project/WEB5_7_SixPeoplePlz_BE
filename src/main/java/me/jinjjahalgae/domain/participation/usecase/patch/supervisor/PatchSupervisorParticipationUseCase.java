package me.jinjjahalgae.domain.participation.usecase.patch.supervisor;

import me.jinjjahalgae.domain.user.User;

public interface PatchSupervisorParticipationUseCase {
    void execute(Long contractId, User user);
}
