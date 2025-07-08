package me.jinjjahalgae.domain.contract.mapper;

import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractType;
import me.jinjjahalgae.domain.user.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ContractMapper {

    public Contract toEntity(User user, LocalDateTime startDate, LocalDateTime endDate, String title, String goal, String penalty, String reward, int life,
                                    int proofPerWeek, boolean oneOff, ContractType type) {
        Contract contract = Contract.builder()
                .user(user)
                .startDate(startDate)
                .endDate(endDate)
                .title(title)
                .goal(goal)
                .penalty(penalty)
                .reward(reward)
                .life(life)
                .proofPerWeek(proofPerWeek)
                .oneOff(oneOff)
                .type(type)
                .build();

        contract.initialize();

        return contract;
    }
}
