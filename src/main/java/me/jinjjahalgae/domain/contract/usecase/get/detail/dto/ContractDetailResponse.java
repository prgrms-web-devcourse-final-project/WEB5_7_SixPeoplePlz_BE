package me.jinjjahalgae.domain.contract.usecase.get.detail.dto;

 import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import me.jinjjahalgae.domain.contract.enums.ContractStatus;
 import me.jinjjahalgae.domain.contract.usecase.get.common.ContractBasicResponse;
 import me.jinjjahalgae.domain.participation.usecase.common.ParticipantSimpleResponse;

/**
 * 계약 상세 response
 * 계약서 미리보기 등 여기저기 사용
 */
@Schema(
    title = "계약 상세 응답",
    description = "계약 상세 정보와 서명 정보를 포함한 응답 DTO"
)
 public record ContractDetailResponse (

        @Schema(description = "계약 조회 공통 데이터")
        ContractBasicResponse contractBasicResponse,

        @Schema(description = "계약 상태", example = "ACTIVE")
        ContractStatus contractStatus, //현재 상태(진행중 등)

        @Schema(description = "현재 인증 수", example = "5")
        int currentProof, //지금까지 성공한 인증 수

        @Schema(description = "현재 인증 횟수 / 총 인증 횟수", example = "5/10")
        String achievementRatio,

        @Schema(description = "경과 기간 / 총 기간 (일)", example = "15/30")
        String periodRatio,

        @Schema(description = "횟수 달성률", example = "50.0")
        double achievementPercent, // 계산해서 제공

        @Schema(description = "기간 달성률", example = "75.0")
        double periodPercent,

        @Schema(description = "참여자 정보", example = "참여자 정보")
        List<ParticipantSimpleResponse> participants, //참여한 유저 정보

        @Schema(description = "오늘자 인증 존재 여부 (오늘자 인증이 없으면 false)", example = "false")
        boolean todayProofExist
) {}
