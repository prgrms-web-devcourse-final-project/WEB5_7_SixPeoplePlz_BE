package me.jinjjahalgae.domain.contract.usecase.get.common;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ContractBasicResponse (
        @Schema(description = "계약 ID", example = "1")
        Long contractId, //계약 id

        @Schema(description = "계약 UUID", example = "123e4567-e89b-12d3-a456-426614174000")
        String contractUuid, //계약 uuid

        @Schema(description = "계약 제목", example = "매일 운동하기")
        String title, //목표 제목

        @Schema(description = "목표 상세", example = "매일 30분 이상 운동하기")
        String goal, //목표 상세

        @Schema(description = "한 주 인증 수", example = "3")
        int proofPerWeek, //설정한 한 주 인증 수

        @Schema(description = "벌칙", example = "치킨 못 먹기")
        String penalty, //벌칙

        @Schema(description = "보상", example = "치킨 먹기")
        String reward, //보상

        @Schema(description = "총 인증 수", example = "10")
        int totalProof, // 총 필요한 인증 수

        @Schema(description = "원본 실패 가능 횟수 (계약서용)", example = "3")
        int totalLife,

        @Schema(description = "시작 날짜", example = "2024-01-01T09:00:00")
        LocalDateTime startDate, //시작 날짜

        @Schema(description = "종료 날짜", example = "2024-01-31T23:59:59")
        LocalDateTime endDate //종료 날짜
) {}