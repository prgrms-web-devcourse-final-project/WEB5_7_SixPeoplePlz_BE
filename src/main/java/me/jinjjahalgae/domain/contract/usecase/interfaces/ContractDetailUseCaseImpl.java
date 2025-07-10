package me.jinjjahalgae.domain.contract.usecase.interfaces;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.contract.dto.response.ContractDetailResponse;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.mapper.ContractMapper;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.contract.usecase.ContractDetailUseCase;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContractDetailUseCaseImpl implements ContractDetailUseCase {

    private final ContractRepository contractRepository;
    private final ContractMapper contractMapper;

    @Override
    public ContractDetailResponse execute(Long userId, Long contractId) {
        Contract contract = findContractByIdAndUserId(contractId, userId);
        return contractMapper.toDetailResponse(contract);
    }

    private Contract findContractByIdAndUserId(Long contractId, Long userId) {
        return contractRepository.findDetailsByIdAndUserId(contractId, userId)
                .orElseThrow(() -> ErrorCode.CONTRACT_NOT_FOUND.domainException("계약을 찾을 수 없습니다."));
    }
}
