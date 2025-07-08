package me.jinjjahalgae.domain.contract.usecase;

public interface ContractListUseCase {
    //계약 목록 조회
    List<ContractListResponse> execute(Long userId);
}
