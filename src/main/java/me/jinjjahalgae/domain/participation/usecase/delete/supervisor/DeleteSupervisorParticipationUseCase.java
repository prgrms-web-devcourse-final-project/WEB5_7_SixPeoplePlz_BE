package me.jinjjahalgae.domain.participation.usecase.delete.supervisor;

import me.jinjjahalgae.domain.user.User;

public interface DeleteSupervisorParticipationUseCase {
    void execute(Long contractId, User user);
}
