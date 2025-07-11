package me.jinjjahalgae.domain.participation.usecase.common;

import io.swagger.v3.oas.annotations.media.Schema;

public record ParticipantFullResponse (
        @Schema(description = "참가자 기본 정보")
        ParticipantSimpleResponse basicInfo,

        @Schema(description = "서명 이미지 키")
        String signatureImageKey
)
{}
