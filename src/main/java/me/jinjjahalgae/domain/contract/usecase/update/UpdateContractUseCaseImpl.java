package me.jinjjahalgae.domain.contract.usecase.update;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractType;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.contract.usecase.update.dto.ContractUpdateRequest;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateContractUseCaseImpl implements UpdateContractUseCase {

    private final ContractRepository contractRepository;

    @Override
    public void execute(Long userId, Long contractId, ContractUpdateRequest request) {
        //수정 권한이 있는 계약자인가?
        Contract contract = findContractByIdAndVerifyContractor(contractId, userId);
        //감독자 서명이 있는가? (시작 전에만 수정이 가능)
        contract.validateUpdatable();
        //계약 수정 진행
        contract.updateContract(
                request.title(),
                request.goal(),
                request.penalty(),
                request.reward(),
                request.life(),
                request.proofPerWeek(),
                request.oneOff(),
                request.startDate(),
                request.endDate(),
                ContractType.valueOf(request.type())
        );
    }

    private Contract findContractByIdAndVerifyContractor(Long contractId, Long userId) {
        //id로 계약을 찾고 없으면 없다고
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> ErrorCode.CONTRACT_NOT_FOUND.domainException("계약을 찾을 수 없습니다."));
        //있으면 계약자인지 확인(권한)
        if (!contract.getUser().getId().equals(userId)) {
            throw ErrorCode.ACCESS_DENIED.domainException("계약에 대한 접근 권한이 없습니다.");
        }
        return contract;
    }
}
