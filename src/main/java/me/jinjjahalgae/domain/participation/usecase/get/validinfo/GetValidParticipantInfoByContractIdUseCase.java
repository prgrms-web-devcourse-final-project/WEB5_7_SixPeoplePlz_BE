package me.jinjjahalgae.domain.participation.usecase.get.validinfo;

import java.util.List;

public interface GetValidParticipantInfoByContractIdUseCase {

    List<ParticipantInfoResponse> execute(long contractId);
}
