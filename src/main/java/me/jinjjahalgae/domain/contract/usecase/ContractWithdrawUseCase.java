package me.jinjjahalgae.domain.contract.usecase;

public interface ContractWithdrawUseCase {
    //계약 중도 포기
    void execute(Long userId, Long contractId);
}
