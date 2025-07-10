package me.jinjjahalgae.domain.contract.usecase.get.list;

import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GetContractListUseCase {
    //계약 목록 조회 (진행중, 종료 히스토리 공유)
    Page<ContractListResponse> execute(Long userId, List<ContractStatus> statuses, Pageable pageable);
}
