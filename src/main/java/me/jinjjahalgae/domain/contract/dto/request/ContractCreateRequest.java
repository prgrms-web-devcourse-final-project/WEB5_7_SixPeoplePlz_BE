package me.jinjjahalgae.domain.contract.dto.request;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import me.jinjjahalgae.domain.contract.enums.ContractType;
//뒤에 메시지는 안적어도 됨, default 메시지 사용


/**
 * 계약 생성 request
 */

@Schema(
    title = "계약 생성 요청",
    description = "새로운 계약을 생성하기 위한 요청 DTO"
)
public record ContractCreateRequest(
    @Schema(description = "계약 제목",
        example = "매일 운동하기",
        requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank (message = "title은 필수 입력 값입니다.")
    String title,
    
    @Schema(description = "목표 상세 설명",
        example = "매일 30분 이상 운동하기", 
        requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank (message = "goal는 필수 입력 값입니다.")
    String goal,

    //선택사항 : 둘 중 하나만 입력
    @Schema(description = "실패 시 벌칙", 
        example = "치킨 못 먹기", 
        requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String penalty,
    
    @Schema(description = "성공 시 보상", 
        example = "치킨 먹기", 
        requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String reward,

    @Schema(description = "실패 가능 횟수", 
        example = "3", 
        requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "life는 필수입니다.")
    @Min(value = 0, message = "life는 0 이상이어야 합니다.")
    Integer life,

    @Schema(description = "주간 인증 횟수", 
        example = "3", 
        requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "proofPerWeek는 필수입니다.")
    @Min(value = 1, message = "proofPerWeek는 1 이상이어야 합니다.")
    Integer proofPerWeek,

    @Schema(description = "당일 계약 여부", 
        example = "false", 
        requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "oneOff는 필수입니다.")
    Boolean oneOff,

    @Schema(description = "계약 시작일", 
        example = "2025-01-01T00:00:00", 
        requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "startDate은 필수입니다.")
    LocalDateTime startDate,

    @Schema(description = "계약 종료일", 
        example = "2025-01-01T00:00:00", 
        requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "endDate은 필수입니다.")
    LocalDateTime endDate,

    @Schema(description = "계약서 디자인 타입", 
        example = "BASIC", 
        requiredMode = Schema.RequiredMode.REQUIRED)
    ContractType type,

    @Schema(description = "서명 이미지 키", 
        example = "signature.jpg", 
        requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "signatureImageKey은 필수입니다.")
    String signatureImageKey //서명 이미지
) {}
