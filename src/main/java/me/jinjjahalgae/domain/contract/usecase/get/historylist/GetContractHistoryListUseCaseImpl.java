package me.jinjjahalgae.domain.contract.usecase.get.historylist;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.mapper.ContractMapper;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.contract.usecase.get.list.dto.ContractListResponse;
import me.jinjjahalgae.domain.contract.usecase.get.historylist.dto.ContractHistoryRequest;
import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import me.jinjjahalgae.domain.participation.enums.Role;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetContractHistoryListUseCaseImpl implements GetContractHistoryListUseCase{

    private final ContractRepository contractRepository;
    private final ContractMapper contractMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ContractListResponse> execute(Long userId, ContractHistoryRequest request, Pageable pageable) {

        // status에는 null, COMPLETED(이행 성공), FAILED(이행 실패), ABANDONED(중간 포기)가능
        // 이게 아닌 status 들어왔으면 ErrorCode.INVALID_REQUEST
        // null이면 COMPLETED | FAILED | ABANDONED를 모두 검색
        ContractStatus status = null;
        if (request.status() != null) {
            try {
                status = ContractStatus.valueOf(request.status());
                if (!List.of(ContractStatus.COMPLETED, ContractStatus.FAILED, ContractStatus.ABANDONED).contains(status)) {
                    throw ErrorCode.INVALID_CONTRACT_STATUS.domainException("status는 COMPLETED, FAILED, ABANDONED 중 하나여야 합니다: " + request.status());
                }
            } catch (IllegalArgumentException e) {
                throw ErrorCode.INVALID_CONTRACT_STATUS.domainException("유효하지 않은 status입니다: " + request.status());
            }
        }

        // Role enum 변환
        Role role;
        try {
            role = Role.valueOf(request.role());
        } catch (IllegalArgumentException e) {
            throw ErrorCode.INVALID_CONTRACT_ROLE.domainException("유효하지 않은 role입니다: " + request.role());
        }

        // 키워드 검색 (null이면 검색 조건에서 제외)
        String keyword = request.keyword();
        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }

        // 계약종료일 < 입력된 날짜인 계약들 (null이면 검색 조건에서 제외)
        // Instant endDate = request.endDate(); // 불필요한 변환 제거
        // repository에서 조건에 맞는 Contract 검색하기
        Page<Contract> contracts = contractRepository.findContractHistoryByConditions(
            userId, role, keyword, request.endDate(), status, pageable
        );

        // mapper에서 mapping해서 리턴
        return contracts.map(contractMapper::toListResponse);
    }
}
