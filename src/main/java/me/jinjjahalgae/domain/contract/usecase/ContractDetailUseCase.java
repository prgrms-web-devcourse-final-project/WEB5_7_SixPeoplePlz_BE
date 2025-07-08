package me.jinjjahalgae.domain.contract.usecase;

public interface ContractDetailUseCase {
    //계약 상세 조회
    ContractDetailResponse execute(Long userId, Long contractId);
}
