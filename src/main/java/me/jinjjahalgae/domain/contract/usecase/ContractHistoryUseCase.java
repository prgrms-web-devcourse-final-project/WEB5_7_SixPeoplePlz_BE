package me.jinjjahalgae.domain.contract.usecase;

public interface ContractHistoryUseCase {
    //종료된 계약 조회
    List<ContractListResponse> execute(Long userId, String type);
}
