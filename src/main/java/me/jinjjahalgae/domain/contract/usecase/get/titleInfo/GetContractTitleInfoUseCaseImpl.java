package me.jinjjahalgae.domain.contract.usecase.get.titleInfo;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.contract.usecase.get.titleInfo.dto.TitleInfoResponse;
import me.jinjjahalgae.domain.participation.repository.ParticipationRepository;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetContractTitleInfoUseCaseImpl implements GetContractTitleInfoUseCase {

    private final ContractRepository contractRepository;
    private final ParticipationRepository participationRepository;

    @Override
    @Transactional(readOnly = true)
    public TitleInfoResponse execute(Long userId, Long contractId) {

        boolean isParticipated = participationRepository.existsByContractIdAndUserIdAndValidIsTrue(contractId, userId);
        if ( !isParticipated ) {
            throw ErrorCode.ACCESS_DENIED.serviceException();
        }

        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(ErrorCode.CONTRACT_NOT_FOUND::serviceException);

        return new TitleInfoResponse(contract.getTitle(), contract.getGoal());
    }
}
