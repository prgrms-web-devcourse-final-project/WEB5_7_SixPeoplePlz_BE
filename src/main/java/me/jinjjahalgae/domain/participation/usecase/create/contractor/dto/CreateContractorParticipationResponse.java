package me.jinjjahalgae.domain.participation.usecase.create.contractor.dto;

import me.jinjjahalgae.domain.participation.enums.Role;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 참여(서명) 응답 DTO
 */
@Schema(
    title = "참여(서명) 응답",
    description = "계약 참여자의 서명 정보를 담은 응답 DTO"
)
public record CreateContractorParticipationResponse(
        @Schema(description = "참여 ID", example = "1")
        Long id,

        @Schema(description = "계약 ID", example = "1")
        Long contractId,

        @Schema(description = "사용자 ID", example = "1")
        Long userId,

        @Schema(description = "사용자 이름", example = "육제발")
        String userName, //혹은 닉네임?

        @Schema(description = "서명 이미지 키", example = "contracts/signatures/supervisor_12345.png")
        String imageKey,

        @Schema(description = "사용자 역할", example = "SUPERVISOR")
        Role role,

        @Schema(description = "서명 유효 여부", example = "true")
        Boolean valid,

        @Schema(description = "서명 일시", example = "2024-01-01T09:00:00")
        LocalDateTime createdAt
) { }