package me.jinjjahalgae.domain.contract.usecase;

public interface ContractUpdateUseCase {
    //계약 수정
    void execute(Long userId, Long contractId, ContractUpdateRequest request);
}