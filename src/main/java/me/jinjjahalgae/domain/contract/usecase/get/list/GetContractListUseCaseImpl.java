package me.jinjjahalgae.domain.contract.usecase.get.list;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import me.jinjjahalgae.domain.contract.mapper.ContractMapper;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.contract.usecase.get.list.dto.ContractListResponse;
import me.jinjjahalgae.domain.participation.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetContractListUseCaseImpl implements GetContractListUseCase {

    private final ContractRepository contractRepository;
    private final ContractMapper contractMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ContractListResponse> execute(Long userId, Role role, Pageable pageable) {

        List<ContractStatus> activeStatuses = List.of(ContractStatus.PENDING, ContractStatus.IN_PROGRESS);

        Page<Contract> contractPage;

        if (role == Role.CONTRACTOR) {
            // 계약자로 참여한 계약 조회
            contractPage = contractRepository.findByUserIdAndStatusInOrderByIdDesc(userId, activeStatuses, pageable);
        } else {
            // 감독자로 참여한 계약 조회
            contractPage = contractRepository.findContractByParticipantUserIdAndRoleAndStatusInOrderByIdDesc(
                    userId, Role.SUPERVISOR, activeStatuses, pageable);
        }

        return contractPage
                .map(contractMapper::toListResponse);
    }
}

