package me.jinjjahalgae.domain.contract.usecase;

public interface ContractCreateUseCase {
    ContractCreateResponse execute (Long userId, ContractCreateRequest request);
}
