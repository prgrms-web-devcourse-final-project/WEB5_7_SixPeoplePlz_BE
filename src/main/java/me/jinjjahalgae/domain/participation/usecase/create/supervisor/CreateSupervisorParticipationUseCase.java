package me.jinjjahalgae.domain.participation.usecase.create.supervisor;

import me.jinjjahalgae.domain.participation.usecase.common.dto.ParticipationCreateRequest;
import me.jinjjahalgae.domain.user.User;

public interface CreateSupervisorParticipationUseCase {
    void execute(Long contractId, ParticipationCreateRequest request, User user);
}
