package me.jinjjahalgae.domain.contract.dto.response;

import java.time.LocalDateTime;

/**
 * 인증 리스트 response
 * 메인, 히스토리 등 계약 목록 조회 응답
 */
public record ContractListResponse(
        Long contractId,
        String contractUuid,
        String title,
        String contractStatus, //계약 현재 상태
        int proofPerWeek,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String reward,
        String penalty,

        double achievementPercent, // 계산해서 제공
        double periodPercent      // 계산해서 제공
) {}