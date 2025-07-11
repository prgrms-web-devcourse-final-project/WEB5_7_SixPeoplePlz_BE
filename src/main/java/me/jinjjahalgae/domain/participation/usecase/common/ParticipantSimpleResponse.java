package me.jinjjahalgae.domain.participation.usecase.common;

import io.swagger.v3.oas.annotations.media.Schema;
import me.jinjjahalgae.domain.participation.enums.Role;

public record ParticipantSimpleResponse (

        @Schema(description = "사용자 ID")
        Long userId,

        @Schema(description = "사용자 이름")
        String userName,

        @Schema(description = "역할")
        Role role,

        @Schema(description = "참여 유효 여부")
        Boolean valid
) {}
