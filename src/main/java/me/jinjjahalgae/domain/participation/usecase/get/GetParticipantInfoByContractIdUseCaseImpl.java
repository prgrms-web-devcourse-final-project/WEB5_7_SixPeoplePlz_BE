package me.jinjjahalgae.domain.participation.usecase.get;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jinjjahalgae.domain.participation.dto.response.ParticipantInfoResponse;
import me.jinjjahalgae.domain.participation.repository.ParticipationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetParticipantInfoByContractIdUseCaseImpl implements GetParticipantInfoByContractIdUseCase {

    private final ParticipationRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<ParticipantInfoResponse> execute(long contractId) {

        return repository.findByContract_Id(contractId).stream()
                .map(ParticipantInfoResponse::from)
                .toList();

    }
}
