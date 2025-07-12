package me.jinjjahalgae.domain.contract.mapper;

import me.jinjjahalgae.domain.contract.usecase.create.dto.CreateContractRequest;
import me.jinjjahalgae.domain.contract.usecase.get.common.ContractBasicResponse;
import me.jinjjahalgae.domain.contract.usecase.get.detail.dto.ContractDetailResponse;
import me.jinjjahalgae.domain.contract.usecase.get.list.dto.ContractListResponse;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractType;
import me.jinjjahalgae.domain.contract.usecase.get.title.dto.ContractTitleInfoResponse;
import me.jinjjahalgae.domain.participation.entity.Participation;
import me.jinjjahalgae.domain.participation.usecase.common.ParticipantSimpleResponse;
import me.jinjjahalgae.domain.user.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ContractMapper {
    public Contract toEntity(User user, CreateContractRequest request) {
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
                contract.calculateAchievementRatio(), // 5/10 형태
                contract.calculatePeriodRatio(), // 15/30 형태
                contract.calculateAchievementPercent(), //횟수 달성률
                contract.calculatePeriodPercent() //기간 달성률
        );
    }

    public ContractDetailResponse toDetailResponse(Contract contract) {
        ContractBasicResponse basicResponse = new ContractBasicResponse(
                contract.getId(),
                contract.getUuid(),
                contract.getTitle(),
                contract.getGoal(),
                contract.getProofPerWeek(),
                contract.getPenalty(),
                contract.getReward(),
                contract.getTotalProof(),
                contract.getLife(),
                contract.getStartDate(),
                contract.getEndDate()
        );

        return new ContractDetailResponse(
                basicResponse,
                contract.getStatus(),
                contract.getCurrentProof(),
                contract.getCurrentFail(),
                contract.getRemainingLife(),
                contract.calculateAchievementRatio(),
                contract.calculatePeriodRatio(),
                contract.calculateAchievementPercent(),
                contract.calculatePeriodPercent(),
                mapToParticipantSimpleResponse(contract)
        );
    }
    private List<ParticipantSimpleResponse> mapToParticipantSimpleResponse(Contract contract) {
        return contract.getParticipations().stream()
                .filter(Participation::getValid)
                .map(participation -> new ParticipantSimpleResponse(
                        participation.getUser().getId(),
                        participation.getUser().getName(),
                        participation.getRole(),
                        participation.getValid()
                ))
                .toList();
    }

    public ContractTitleInfoResponse toTitleInfoResponse(Contract contract) {
        return new ContractTitleInfoResponse(
                contract.getTitle(),
                contract.getGoal()
        );
    }
}