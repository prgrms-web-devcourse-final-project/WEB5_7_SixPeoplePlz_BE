package me.jinjjahalgae.domain.contract.usecase.get.detail;

public interface GetContractDetailUseCase {
    //계약 상세 조회
    ContractDetailResponse execute(Long userId, Long contractId);
}
