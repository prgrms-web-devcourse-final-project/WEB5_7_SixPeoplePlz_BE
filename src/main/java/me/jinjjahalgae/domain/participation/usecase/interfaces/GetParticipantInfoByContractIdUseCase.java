package me.jinjjahalgae.domain.participation.usecase.interfaces;

import me.jinjjahalgae.domain.participation.dto.ParticipantInfoResponse;

import java.util.List;

public interface GetParticipantInfoByContractIdUseCase {

    List<ParticipantInfoResponse> execute(long contractId);
}
