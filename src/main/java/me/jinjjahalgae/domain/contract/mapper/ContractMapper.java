package me.jinjjahalgae.domain.contract.mapper;

import me.jinjjahalgae.domain.contract.dto.request.ContractCreateRequest;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.user.User;
import org.springframework.stereotype.Component;

@Component
public class ContractMapper {
    public static Contract toEntity(User user, ContractCreateRequest request) {
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
                .type(request.type())
                .build();

        contract.initialize();

        return contract;
    }
}
