package me.jinjjahalgae.domain.contract.mapper;

import me.jinjjahalgae.domain.contract.dto.request.ContractCreateRequest;
import me.jinjjahalgae.domain.contract.dto.response.ContractListResponse;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractType;
import me.jinjjahalgae.domain.user.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ContractMapper {
    public Contract toEntity(User user, ContractCreateRequest request) {
        Contract contract = Contract.builder()
                .user(user)
                .startDate(request.startDate())
                .endDate(request.endDate())
                .title(request.title())
                .goal(request.goal())
                .penalty(request.penalty())
                .reward(request.reward())
                .life(request.life())
                .proofPerWeek(request.proofPerWeek())
                .oneOff(request.oneOff())
                .type(ContractType.valueOf(String.valueOf(request.type())))
                .build();

        contract.initialize();

        return contract;
    }

    public ContractListResponse toListResponse(Contract contract) {
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
