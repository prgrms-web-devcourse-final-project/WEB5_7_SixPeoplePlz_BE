package me.jinjjahalgae.domain.contract.dto.request;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import me.jinjjahalgae.domain.contract.enums.ContractType;
import me.jinjjahalgae.global.validation.EnumValue;

/**
 * 계약 수정 request
 * 기본적으로 생성과 동일하지만 서명은 다시 받지 않음.
 */
@Schema(
    title = "계약 수정 요청",
    description = "기존 계약을 수정하기 위한 요청 DTO (감독자 서명 전에만 가능)"
)
public record ContractUpdateRequest(
    @Schema(description = "계약 제목", 
        example = "매일 운동하기", 
        requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank (message = "title은 필수 입력 값입니다.")
    String title,

    @Schema(description = "목표 상세 설명", 
        example = "매일 1시간 이상 운동하기", 
        requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "goal은 필수 입력 값입니다.")
    String goal,

    @Schema(description = "실패 시 벌칙", 
        example = "치킨 2번 못 먹기", 
        requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String penalty,

    @Schema(description = "성공 시 보상", 
        example = "치킨 2번 먹기", 
        requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String reward,

    @Schema(description = "실패 가능 횟수", 
        example = "5", 
        requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "life는 필수입니다.")
    @Min(value = 0, message = "life는 0 이상이어야 합니다.")
    Integer life,

    @Schema(description = "주간 인증 횟수",
        example = "7", 
        requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "proofPerWeek은 필수입니다.")
    @Min(value = 1, message = "proofPerWeek은 1 이상이어야 합니다.")
    Integer proofPerWeek,

    @Schema(description = "당일 계약 여부", 
        example = "false", 
        requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "oneOff는 필수입니다.")
    Boolean oneOff,

    @Schema(description = "계약 시작일", 
        example = "2024-01-01T09:00:00", 
        requiredMode = Schema.RequiredMode.REQUIRED) 
    @NotNull(message = "startDate은 필수입니다.")
    LocalDateTime startDate,

    @Schema(description = "계약 종료일", 
        example = "2024-01-31T23:59:59", 
        requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "endDate은 필수입니다.")
    LocalDateTime endDate,

    @Schema(description = "계약서 디자인 타입", 
        example = "BASIC", 
        requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "type은 필수입니다.")
    @EnumValue(enumClass = ContractType.class)
    String type
) {}
