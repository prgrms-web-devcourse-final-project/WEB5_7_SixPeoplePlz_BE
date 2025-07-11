package me.jinjjahalgae.domain.contract.usecase.get.detail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import me.jinjjahalgae.domain.contract.usecase.get.common.ContractBasicResponse;
import me.jinjjahalgae.domain.participation.usecase.common.ParticipantSimpleResponse;

import java.util.List;

public record RealContractDetailResponse (

        @Schema(description = "계약 조회 공통 데이터")
        ContractBasicResponse contractBasicResponse,

        @Schema(description = "계약 상태", example = "ACTIVE")
        ContractStatus contractStatus, //현재 상태(진행중 등)

        @Schema(description = "현재 인증 수", example = "5")
        int currentProof, //지금까지 성공한 인증 수

        @Schema(description = "현재 실패한 횟수", example = "1")
        int currentFail,

        @Schema(description = "남은 실패 수", example = "2")
        int remainingLife, //남아있는 실패 수

        @Schema(description = "현재 인증 횟수 / 총 인증 횟수", example = "5/10")
        String achievementRatio,

        @Schema(description = "경과 기간 / 총 기간 (일)", example = "15/30")
        String periodRatio,

        @Schema(description = "횟수 달성률", example = "50.0")
        double achievementPercent, // 계산해서 제공

        @Schema(description = "기간 달성률", example = "75.0")
        double periodPercent,
        // 계산해서 제공
        @Schema(description = "참여자 정보", example = "참여자 정보")
        List<ParticipantSimpleResponse> participants //참여한 유저 정보
) {}
