package me.jinjjahalgae.domain.contract.usecase.get.list;

import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import me.jinjjahalgae.domain.contract.usecase.get.list.dto.ContractListResponse;
import me.jinjjahalgae.domain.participation.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GetContractListUseCase {
    /*계약 목록 조회 : 역할로 구분
    계약자 -> 대기중 ,진행중
    감독자 -> 대기중, 진행중
     */
    Page<ContractListResponse> execute(Long userId, Role role, Pageable pageable);
}
