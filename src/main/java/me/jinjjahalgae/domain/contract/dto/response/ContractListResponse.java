package me.jinjjahalgae.domain.contract.dto.response;

import me.jinjjahalgae.domain.contract.enums.ContractStatus;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 계약 리스트 response
 * 메인, 히스토리 등 계약 목록 조회 응답
 */
@Schema(
    title = "계약 리스트 응답",
    description = "계약 목록 조회 시 반환되는 응답 DTO, 메인 페이지, 히스토리 페이지 등에 사용(계약 상태로 구분분)"
)
public record ContractListResponse(
        @Schema(description = "계약 ID", example = "1")
        Long contractId,

        @Schema(description = "계약 UUID", example = "123e4567-e89b-12d3-a456-426614174000")
        String contractUuid,

        @Schema(description = "계약 제목", example = "매일 운동하기")
        String title,

        @Schema(description = "계약 상태", example = "ACTIVE")
        ContractStatus contractStatus, //계약 현재 상태

        @Schema(description = "한 주 인증 수", example = "3")
        int proofPerWeek,

        @Schema(description = "계약 시작 날짜", example = "2024-01-01T09:00:00")
        LocalDateTime startDate,

        @Schema(description = "계약 종료 날짜", example = "2024-01-31T23:59:59")
        LocalDateTime endDate,

        @Schema(description = "보상", example = "치킨 먹기")
        String reward,

        @Schema(description = "벌칙", example = "치킨 못 먹기")
        String penalty,

        @Schema(description = "횟수 달성률", example = "50.0")
        double achievementPercent, // 계산해서 제공

        @Schema(description = "기간 달성률", example = "75.0")
        double periodPercent      // 계산해서 제공
) {}