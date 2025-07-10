package me.jinjjahalgae.domain.contract.usecase.update;

public interface UpdateContractUseCase {
    //계약 수정
    void execute(Long userId, Long contractId, ContractUpdateRequest request);
}