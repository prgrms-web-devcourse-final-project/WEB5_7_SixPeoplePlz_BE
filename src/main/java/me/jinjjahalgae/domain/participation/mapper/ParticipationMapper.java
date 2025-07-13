package me.jinjjahalgae.domain.participation.mapper;

import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.participation.usecase.create.contractor.dto.CreateContractorParticipationResponse;
import me.jinjjahalgae.domain.participation.entity.Participation;
import me.jinjjahalgae.domain.participation.enums.Role;
import me.jinjjahalgae.domain.user.User;
import org.springframework.stereotype.Component;

@Component
public class ParticipationMapper {
    public Participation toEntity(Contract contract, User user, String imageKey, Role role, Boolean valid) {
        return Participation.builder()
                .contract(contract)
                .user(user)
                .imageKey(imageKey)
                .role(role)
                .valid(valid)
                .build();
    }

    public CreateContractorParticipationResponse from(Participation participation) {
        return new CreateContractorParticipationResponse(
                participation.getId(),
                participation.getContract().getId(),
                participation.getUser().getId(),
                participation.getUser().getName(),
                participation.getImageKey(),
                participation.getRole(),
                participation.getValid(),
                participation.getCreatedAt()
        );
    }
}
