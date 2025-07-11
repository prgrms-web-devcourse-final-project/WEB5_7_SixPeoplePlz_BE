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
        // 계약 조회
        Contract contract = contractRepository.findByIdWithUser(contractId)
                .orElseThrow(() -> ErrorCode.CONTRACT_NOT_FOUND.domainException("존재하지 않는 계약입니다."));

        // 계약자 권한 검증
        contract.validateContractor(userId);

        //감독자 서명이 있는가? (시작 전에만 수정이 가능)
        contract.validateUpdatable();
        //계약 수정 진행
        contract.update(
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
}
