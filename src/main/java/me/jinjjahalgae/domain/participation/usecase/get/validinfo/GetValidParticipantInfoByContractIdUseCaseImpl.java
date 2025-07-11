package me.jinjjahalgae.domain.participation.usecase.get.validinfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jinjjahalgae.domain.participation.entity.Participation;
import me.jinjjahalgae.domain.participation.repository.ParticipationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetValidParticipantInfoByContractIdUseCaseImpl implements GetValidParticipantInfoByContractIdUseCase {

    private final ParticipationRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<ParticipantInfoResponse> execute(long contractId) {

        return repository.findByContract_Id(contractId).stream()
                .filter(Participation::getValid)
                .map(ParticipantInfoResponse::from)
                .toList();

    }
}
