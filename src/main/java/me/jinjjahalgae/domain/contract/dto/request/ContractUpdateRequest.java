package me.jinjjahalgae.domain.contract.dto.request;

import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import me.jinjjahalgae.domain.contract.enums.ContractType;

/**
 * 계약 수정 request
 * 기본적으로 생성과 동일하지만 서명은 다시 받지 않음.
 */

public record ContractUpdateRequest(
    @NotBlank String title,
    @NotBlank String goal,
    String penalty,
    String reward,
    @NotNull int life,
    @NotNull int proofPerWeek,
    @NotNull boolean oneOff,
    @NotNull LocalDateTime startDate,
    @NotNull LocalDateTime endDate,
    @NotNull ContractType type
) {}
