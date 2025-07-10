package me.jinjjahalgae.domain.participation.usecase.get.participations;

import java.util.List;

public interface GetParticipantInfoByContractIdUseCase {

    List<ParticipantInfoResponse> execute(long contractId);
}
