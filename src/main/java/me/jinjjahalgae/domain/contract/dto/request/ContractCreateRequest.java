package me.jinjjahalgae.domain.contract.dto.request;

import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import me.jinjjahalgae.domain.contract.enums.ContractType;

/**
 * 계약 생성 request
 */

public record ContractCreateRequest(
    @NotBlank String title,
    @NotBlank String goal,
    String penalty,
    String reward,
    @NotNull int life,
    @NotNull int proofPerWeek,
    @NotNull boolean oneOff,
    @NotNull LocalDateTime startDate,
    @NotNull LocalDateTime endDate,
    @NotNull ContractType type,
    @NotBlank String signatureImageKey //서명 이미지
) {}
