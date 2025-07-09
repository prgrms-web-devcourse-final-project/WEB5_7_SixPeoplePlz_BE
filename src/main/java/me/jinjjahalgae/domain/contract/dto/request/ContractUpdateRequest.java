package me.jinjjahalgae.domain.contract.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import me.jinjjahalgae.domain.contract.enums.ContractType;

/**
 * 계약 수정 request
 * 기본적으로 생성과 동일하지만 서명은 다시 받지 않음.
 */

public record ContractUpdateRequest(
    @NotBlank (message = "제목은 필수 입력 값입니다.")
    String title,

    @NotBlank(message = "목표는 필수 입력 값입니다.")
    String goal,

    String penalty,
    String reward,

    @NotNull(message = "실패 가능 횟수는 필수입니다.")
    @Min(value = 0, message = "실패 가능 횟수는 0 이상이어야 합니다.")
    Integer life,

    @NotNull(message = "주간 인증 횟수는 필수입니다.")
    @Min(value = 1, message = "주간 인증 횟수는 1 이상이어야 합니다.")
    Integer proofPerWeek,

    @NotNull(message = "당일 여부는 필수입니다.")
    Boolean oneOff,

    @NotNull(message = "시작일은 필수입니다.")
    LocalDateTime startDate,

    @NotNull(message = "종료일은 필수입니다.")
    LocalDateTime endDate,

    @NotNull(message = "계약서 디자인인 타입은 필수입니다.")
    ContractType type
) {}
