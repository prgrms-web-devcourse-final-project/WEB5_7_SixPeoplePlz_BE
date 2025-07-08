package me.jinjjahalgae.domain.participation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ParticipationCreateRequest (
    @NotBlank String imageKey
) {}
