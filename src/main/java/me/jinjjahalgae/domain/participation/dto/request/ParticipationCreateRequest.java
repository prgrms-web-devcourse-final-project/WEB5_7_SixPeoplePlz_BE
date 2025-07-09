package me.jinjjahalgae.domain.participation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ParticipationCreateRequest (
    @NotBlank(message = "서명 이미지는 필수입니다.")
    String imageKey
) {}
