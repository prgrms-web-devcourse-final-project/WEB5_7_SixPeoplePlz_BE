package me.jinjjahalgae.domain.contract.usecase.get.list;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import me.jinjjahalgae.domain.contract.mapper.ContractMapper;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.contract.usecase.get.list.dto.ContractListResponse;
import me.jinjjahalgae.domain.participation.enums.Role;
import me.jinjjahalgae.domain.proof.repository.ProofRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetContractListUseCaseImpl implements GetContractListUseCase {

    private final ContractRepository contractRepository;
    private final ProofRepository proofRepository;
    private final ContractMapper contractMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ContractListResponse> execute(Long userId, Role role, Pageable pageable) {

        List<ContractStatus> activeStatuses = List.of(ContractStatus.PENDING, ContractStatus.IN_PROGRESS, ContractStatus.WAIT_RESULT);

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
                .map(contract -> {
                    boolean todayProofExist = todayProofExist(contract.getId());
                    return contractMapper.toListResponse(contract, todayProofExist);
                });
    }

    private boolean todayProofExist(Long contractId) {
        ZoneId seoulZone = ZoneId.of("Asia/Seoul");
        LocalDate today = ZonedDateTime.now(seoulZone).toLocalDate();
        Instant startOfDay = today.atStartOfDay(seoulZone).toInstant();
        Instant endOfDay = today.plusDays(1).atStartOfDay(seoulZone).toInstant();

        return proofRepository.existsByContractIdAndCreatedAtToday(contractId, startOfDay, endOfDay);
    }
}

