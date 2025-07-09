package me.jinjjahalgae.domain.contract.usecase;

import me.jinjjahalgae.domain.contract.dto.response.ContractDetailResponse;

public interface ContractDetailUseCase {
    //계약 상세 조회
    ContractDetailResponse execute(Long userId, Long contractId);
}
