package me.jinjjahalgae.domain.contract.usecase.get.title.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 계약 제목, 목표 응답
 * 감독자 인증하러 가기 화면에서 간단한 정보 표시용
 */
@Schema(
        title = "계약 제목 정보 응답",
        description = "계약의 제목과 목표만 포함한 간단한 정보 응답 DTO"
)
public record ContractTitleInfoResponse (
        @Schema(description = "계약 제목", example = "매일 운동하기")
        String title,

        @Schema(description = "계약 목표", example = "매일 30분 이상 운동하기")
        String goal
){}
