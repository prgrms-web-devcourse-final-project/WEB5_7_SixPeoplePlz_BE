package me.jinjjahalgae.domain.participation.dto.response;

import me.jinjjahalgae.domain.participation.entity.Participation;
import me.jinjjahalgae.domain.participation.enums.Role;
import java.time.LocalDateTime;

public record ParticipationResponse(
        Long id,
        Long contractId,
        Long userId,
        String userName, //혹은 닉네임?
        String imageKey,
        Role role,
        Boolean validate,
        LocalDateTime createdAt
) {
    public static ParticipationResponse from(Participation participation) {
        return new ParticipationResponse(
                participation.getId(),
                participation.getContract().getId(),
                participation.getUser().getId(),
                participation.getUser().getName(),
                participation.getImageKey(),
                participation.getRole(),
                participation.getValidate(),
                participation.getCreatedAt()
        );
    }
}