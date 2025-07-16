package me.jinjjahalgae.domain.contract.mapper;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.contract.usecase.create.dto.CreateContractRequest;
import me.jinjjahalgae.domain.contract.usecase.get.common.ContractBasicResponse;
import me.jinjjahalgae.domain.contract.usecase.get.detail.dto.ContractDetailResponse;
import me.jinjjahalgae.domain.contract.usecase.get.list.dto.ContractListResponse;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractType;
import me.jinjjahalgae.domain.contract.usecase.get.preview.dto.ContractPreviewResponse;
import me.jinjjahalgae.domain.contract.usecase.get.title.dto.ContractTitleInfoResponse;
import me.jinjjahalgae.domain.participation.entity.Participation;
import me.jinjjahalgae.domain.participation.mapper.ParticipationMapper;
import me.jinjjahalgae.domain.participation.usecase.common.ParticipantSimpleResponse;
import me.jinjjahalgae.domain.user.User;
import me.jinjjahalgae.global.util.DateTimeConverter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ContractMapper {

    private final ParticipationMapper participationMapper;

    public Contract toEntity(User user, CreateContractRequest request) {
        Contract contract = Contract.builder()
                .user(user)
                .startDate(request.startDate())
                .endDate(request.endDate())
                .title(request.title())
                .goal(request.goal())
                .penalty(request.penalty())
                .reward(request.reward())
                .totalProof(request.totalProof())
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

    public ContractBasicResponse toBasicResponse(Contract contract) {
        return new ContractBasicResponse(
                contract.getId(),
                contract.getUuid(),
                contract.getTitle(),
                contract.getGoal(),
                contract.getPenalty(),
                contract.getReward(),
                contract.getTotalProof(),
                contract.getStartDate(),
                contract.getEndDate()
        );
    }

    public ContractDetailResponse toDetailResponse(Contract contract) {

        ContractBasicResponse basicResponse = toBasicResponse(contract);
        return new ContractDetailResponse(
                basicResponse,
                contract.getStatus(),
                contract.getCurrentProof(),
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

    public ContractPreviewResponse mapToContractPreviewResponse(Contract contract, List<Participation> participationList) {
        return new ContractPreviewResponse(
            toBasicResponse(contract),
            contract.getType(),
            participationList.stream()
                    .map(participationMapper::mapToFullResponse)
                    .toList()
        );
    }
}