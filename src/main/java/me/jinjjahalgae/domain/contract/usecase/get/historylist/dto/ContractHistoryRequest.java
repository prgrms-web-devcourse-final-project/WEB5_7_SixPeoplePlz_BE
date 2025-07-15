package me.jinjjahalgae.domain.contract.usecase.get.historylist.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import me.jinjjahalgae.domain.participation.enums.Role;
import me.jinjjahalgae.global.validation.EnumValue;

import java.time.Instant;

/**
 * 끝난 계약 히스토리 목록에서 검색용
 * @param role not null. 계약자/감독자
 * @param keyword nullable. 계약 제목 검색
 * @param endDate nullable. 계약 종료일 < endDate인 계약 검색용
 * @param status nullable. 계약 상태로 필터링용
 */
public record ContractHistoryRequest(

        @Schema(description = "조회할 역할 (계약자/감독자)")
        @NotNull(message = "조회할 역할(계약자/감독자)는 필수입니다")
        @EnumValue(enumClass = Role.class, message = "role은 CONTRACTOR, SUPERVISOR 중 하나여야 합니다.")
        String role,

        @Schema(description = "검색 키워드", example = "운동")
        String keyword,

        @Schema(description = "종료일", example = "2025-07-11T23:59:59Z")
        Instant endDate,

        @Schema(description = "계약 타입", example = "PENDING")
        @EnumValue(enumClass = ContractStatus.class, message = "type은 PENDING, IN_PROGRESS, COMPLETED, FAILED, ABANDONED 중 하나여야 합니다.")
        String status
) {
}
