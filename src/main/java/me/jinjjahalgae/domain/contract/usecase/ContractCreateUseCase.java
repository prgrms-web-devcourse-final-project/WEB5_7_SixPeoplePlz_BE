package me.jinjjahalgae.domain.contract.usecase;

import me.jinjjahalgae.domain.contract.dto.request.ContractCreateRequest;
import me.jinjjahalgae.domain.contract.dto.response.ContractCreateResponse;

public interface ContractCreateUseCase {
    ContractCreateResponse execute (Long userId, ContractCreateRequest request);
}
