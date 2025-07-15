package me.jinjjahalgae.domain.contract.usecase.get.title;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.mapper.ContractMapper;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.contract.usecase.get.title.dto.ContractTitleInfoResponse;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetContractTitleInfoUseCaseImpl implements GetContractTitleInfoUseCase {

    private final ContractRepository contractRepository;
    private final ContractMapper contractMapper;

    @Override
    @Transactional(readOnly = true)
    public ContractTitleInfoResponse execute(Long userId, Long contractId) {

        //실제 있는 계약인가?
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> ErrorCode.CONTRACT_NOT_FOUND.domainException("존재하지 않는 계약입니다."));

        //그 계약에 대한 유효한 참가자인가?
        contractRepository.findValidParticipantByIdAndUserId(contractId, userId)
                .orElseThrow(() -> ErrorCode.ACCESS_DENIED.domainException("계약에 대한 접근 권한이 없습니다."));

        //두가지 모두 문제없이 만족했다면 조회
        return contractMapper.toTitleInfoResponse(contract);
    }
}
