package me.jinjjahalgae.domain.contract.usecase;

import me.jinjjahalgae.domain.contract.enums.ContractStatus;

import java.util.List;

public interface ContractListUseCase {
    //계약 목록 조회 (진행중, 종료 히스토리 공유)
    List<ContractListResponse> execute(Long userId, List<ContractStatus> statuses);
}
