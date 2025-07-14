package me.jinjjahalgae.domain.contract.usecase.get.detail;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.mapper.ContractMapper;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.contract.usecase.get.detail.dto.ContractDetailResponse;
import me.jinjjahalgae.domain.participation.repository.ParticipationRepository;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetContractDetailUseCaseImpl implements GetContractDetailUseCase {

    private final ContractRepository contractRepository;
    private final ContractMapper contractMapper;
    private final ParticipationRepository participationRepository;

    /// 본인이 계약자인 계약 개요 카드에 보이는 정보
    @Override
    public ContractDetailResponse execute(Long userId, Long contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> ErrorCode.CONTRACT_NOT_FOUND.domainException("존재하지 않는 계약 id: " + contractId));

        // 계약 id, userid가 일치하고 valid인 참여가 있는지 확인
        boolean isParticipated = participationRepository.existsByContractIdAndUserIdAndValidIsTrue(contractId, userId);
        if (!isParticipated) {
            throw ErrorCode.ACCESS_DENIED.serviceException("계약에 접근권한 없음");
        }

        return contractMapper.toDetailResponse(contract);
    }
}
