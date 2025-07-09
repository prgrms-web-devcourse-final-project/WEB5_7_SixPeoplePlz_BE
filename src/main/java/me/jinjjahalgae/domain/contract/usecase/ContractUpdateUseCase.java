package me.jinjjahalgae.domain.contract.usecase;

import me.jinjjahalgae.domain.contract.dto.request.ContractUpdateRequest;

public interface ContractUpdateUseCase {
    //계약 수정
    void execute(Long userId, Long contractId, ContractUpdateRequest request);
}