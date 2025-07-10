package me.jinjjahalgae.domain.participation.usecase.interfaces;

import me.jinjjahalgae.domain.user.User;

public interface DeleteSupervisorParticipationUseCase {
    void execute(Long contractId, User user);
}
