package me.jinjjahalgae.domain.contract.usecase.get.historylist;

import me.jinjjahalgae.domain.contract.usecase.get.list.dto.ContractListResponse;
import me.jinjjahalgae.domain.contract.usecase.get.historylist.dto.ContractHistoryRequest;
import me.jinjjahalgae.domain.participation.enums.Role;


public interface GetContractHistoryListUseCase {

    public ContractListResponse getContractHistoryList(Long userId, Role role, ContractHistoryRequest request);
}
