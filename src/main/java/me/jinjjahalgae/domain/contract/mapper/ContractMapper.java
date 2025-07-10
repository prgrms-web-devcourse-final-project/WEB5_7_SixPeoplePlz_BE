package me.jinjjahalgae.domain.contract.mapper;

import me.jinjjahalgae.domain.contract.usecase.create.dto.CreateContractRequest;
import me.jinjjahalgae.domain.contract.usecase.get.detail.dto.ContractDetailResponse;
import me.jinjjahalgae.domain.contract.usecase.get.list.dto.ContractListResponse;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractType;
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
                contract.calculateAchievementPercent(), //횟수 달성률
                contract.calculatePeriodPercent() //기간 달성률
        );
    }

    public ContractDetailResponse toDetailResponse(Contract contract) {
        return new ContractDetailResponse(
                contract.getId(),
                contract.getUuid(),
                contract.getTitle(),
                contract.getGoal(),
                contract.getPenalty(),
                contract.getReward(),
                contract.getType(),
                contract.getStartDate(),
                contract.getEndDate(),
                contract.getStatus(),
                contract.getProofPerWeek(),
                contract.getTotalProof(),
                contract.getCurrentProof(),
                contract.getLife(), // remainingLife
                contract.calculateAchievementPercent(),
                contract.calculatePeriodPercent(),
                mapToParticipantSimpleResponse(contract) // 참여자 정보
        );
    }

    private List<ContractDetailResponse.ParticipantSimpleResponse> mapToParticipantSimpleResponse(Contract contract) {
        return contract.getParticipations().stream()
                .map(participation -> new ContractDetailResponse.ParticipantSimpleResponse(
                        participation.getUser().getId(),
                        participation.getUser().getName(),
                        participation.getRole(),
                        participation.getImageKey()
                ))
                .toList();
    }
}
