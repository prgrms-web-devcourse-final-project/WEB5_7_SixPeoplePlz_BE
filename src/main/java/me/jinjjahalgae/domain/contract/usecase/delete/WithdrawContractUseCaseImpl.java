package me.jinjjahalgae.domain.contract.usecase.delete;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WithdrawContractUseCaseImpl implements WithdrawContractUseCase {

    private final ContractRepository contractRepository;

    @Override
    @Transactional
    public void execute(Long userId, Long contractId) {

        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> ErrorCode.CONTRACT_NOT_FOUND.domainException("존재하지 않는 계약입니다."));

        //유저 검증 -> 계약자인가?유저의 계약이 아닐 경우 예외 : 엔티티 책임
        contract.validateContractor(userId);

        //계약이 진행중인가? (중도 포기는 진행중인 계약만 가능)
        //계약 상태가 진해중이 아니면 예외, 진행중이면 계약 상태 변경 (진행중 -> 포기)
        contract.withdraw();
    }
}
