package me.jinjjahalgae.domain.contract.usecase.get.detail;

import me.jinjjahalgae.domain.contract.usecase.get.detail.dto.ContractDetailResponse;

public interface GetContractDetailUseCase {
    //계약 상세 조회
    ContractDetailResponse execute(Long userId, Long contractId);
}
