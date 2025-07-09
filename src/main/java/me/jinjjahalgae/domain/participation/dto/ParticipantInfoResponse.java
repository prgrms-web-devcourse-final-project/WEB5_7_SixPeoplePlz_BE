package me.jinjjahalgae.domain.participation.dto;

import me.jinjjahalgae.domain.participation.entity.Participation;
import me.jinjjahalgae.domain.participation.enums.Role;

public record ParticipantInfoResponse(
        String userName,
        Long userId,
        Role role
) {

    /// Participation -> ParticipantInfoResponse로 변환
    public static ParticipantInfoResponse from (Participation participation) {
        return new ParticipantInfoResponse(
                participation.getUser().getName(),
                participation.getUser().getId(),
                participation.getRole()
        );
    }
}
