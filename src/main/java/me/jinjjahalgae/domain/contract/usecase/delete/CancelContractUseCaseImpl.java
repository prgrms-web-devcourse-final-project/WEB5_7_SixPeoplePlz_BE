package me.jinjjahalgae.domain.contract.usecase.delete;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CancelContractUseCaseImpl implements CancelContractUseCase {

    private final ContractRepository contractRepository;

    @Override
    @Transactional
    public void execute(Long userId, Long contractId) {
        //유저 확인
        //권한 확인 -> 기존 다른 확인 부분과 같음
        Contract contract = contractRepository.findByIdWithUser(contractId)
                .orElseThrow(() -> ErrorCode.CONTRACT_NOT_FOUND.domainException("존재하지 않는 계약입니다."));

        //유저 검증 -> 계약자인가?
        contract.validateContractor(userId);

        //계약이 시작했는가 (계약 취소는 대기 상태에서만 가능) + 그렇다면 취소 및 삭제
        contract.cancel();
        contractRepository.delete(contract);
    }
}
