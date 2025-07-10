package me.jinjjahalgae.domain.participation.usecase.interfaces;

import me.jinjjahalgae.domain.participation.dto.request.ParticipationCreateRequest;
import me.jinjjahalgae.domain.user.User;

public interface CreateSupervisorParticipationUseCase {
    void execute(Long contractId, ParticipationCreateRequest request, User user);
}
