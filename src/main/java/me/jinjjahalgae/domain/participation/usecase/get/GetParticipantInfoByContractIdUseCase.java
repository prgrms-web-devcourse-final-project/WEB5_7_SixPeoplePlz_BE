package me.jinjjahalgae.domain.participation.usecase.get;

import me.jinjjahalgae.domain.participation.dto.response.ParticipantInfoResponse;

import java.util.List;

public interface GetParticipantInfoByContractIdUseCase {

    List<ParticipantInfoResponse> execute(long contractId);
}
