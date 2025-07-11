package me.jinjjahalgae.domain.contract.usecase.delete;

public interface CancelContractUseCase {
    //계약 시작 전 취소(포기)
    void execute(Long userId, Long contractId);
}