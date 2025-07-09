package me.jinjjahalgae.domain.participation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 참여(서명) 생성 요청 DTO
 */
@Schema(
    title = "계약자 서명 생성 요청",
    description = "계약자가 계약 생성시에 자신이 서명하기 위한 요청 DTO"
)
public record ParticipationCreateRequest (
    @Schema(description = "서명 이미지 키", 
        example = "contracts/signatures/supervisor_12345.png", 
        requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "imageKey는 필수입니다.")    
    String imageKey //서명 이미지
) {}
