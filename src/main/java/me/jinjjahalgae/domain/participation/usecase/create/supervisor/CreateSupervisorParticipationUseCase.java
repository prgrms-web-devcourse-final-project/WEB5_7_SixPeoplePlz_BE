package me.jinjjahalgae.domain.participation.usecase.create.supervisor;

import me.jinjjahalgae.domain.participation.usecase.create.contractor.dto.CreateContractorParticipationRequest;
import me.jinjjahalgae.domain.user.User;

public interface CreateSupervisorParticipationUseCase {
    void execute(Long contractId, CreateContractorParticipationRequest request, User user);
}
