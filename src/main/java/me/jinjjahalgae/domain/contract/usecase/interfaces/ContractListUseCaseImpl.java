package me.jinjjahalgae.domain.contract.usecase.interfaces;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.contract.dto.response.ContractListResponse;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.contract.usecase.ContractListUseCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ContractListUseCaseImpl implements ContractListUseCase {

    private final ContractRepository contractRepository;

    @Override
    public Page<ContractListResponse> execute(Long userId, List<ContractStatus> statuses, Pageable pageable) {
        //유저 id와 계약의 상태를 받아서 조회 -> 레포지토리에 필요, 페이징으로 구현
        Page<Contract> contractPage = contractRepository.findByUserIdAndStatusInOrderByIdDesc(userId, statuses, pageable);

        return contractPage
                .map(this::mapToResponse);
    }

    private ContractListResponse mapToResponse(Contract contract) {
        return new ContractListResponse(
                contract.getId(),
                contract.getUuid(),
                contract.getTitle(),
                contract.getStatus(),
                contract.getProofPerWeek(),
                contract.getStartDate(),
                contract.getEndDate(),
                contract.getReward(),
                contract.getPenalty(),
                calculateAchievementPercent(contract), //횟수 달성률
                calculatePeriodPercent(contract) //기간 달성률
        );
    }

    private double calculateAchievementPercent(Contract contract) {
        return ( (double) contract.getCurrentProof() / contract.getTotalProof() * 100);
    }

    private double calculatePeriodPercent(Contract contract) {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = contract.getStartDate();
        LocalDateTime endDate = contract.getEndDate();

        long totalDays = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        long passedDays = java.time.temporal.ChronoUnit.DAYS.between(startDate, now) +1;

        return ( (double) passedDays / totalDays * 100);
    }
}
