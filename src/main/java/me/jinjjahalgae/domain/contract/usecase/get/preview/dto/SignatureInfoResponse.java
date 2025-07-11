package me.jinjjahalgae.domain.contract.usecase.get.preview.dto;

import me.jinjjahalgae.domain.participation.entity.Participation;
import me.jinjjahalgae.domain.participation.enums.Role;

public record SignatureInfoResponse(
        String name,
        Role role,
        String imageKey
) {
    public static SignatureInfoResponse from(Participation participation) {
        return new SignatureInfoResponse(
                participation.getUser().getName(),
                participation.getRole(),
                participation.getImageKey()
        );
    }
}
