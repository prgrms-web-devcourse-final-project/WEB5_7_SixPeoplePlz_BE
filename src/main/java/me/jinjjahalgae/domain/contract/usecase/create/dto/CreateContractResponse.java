package me.jinjjahalgae.domain.contract.usecase.create.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 인증 생성 response
 * 생성 완료 응답에는 많은 정보가 필요하지 않지만 id와 uuid를 볼 수 있게 하기 위해서 구현
 */
@Schema(
    title = "단순 계약 생성 성공 응답",
    description = "계약 생성 성공 시 반환되는 응답 DTO"
)
public record CreateContractResponse(
        @Schema(description = "계약 ID", example = "1")
        Long contractId,
        
        @Schema(description = "계약 UUID", example = "123e4567-e89b-12d3-a456-426614174000")
        String contractUuid
) {}