package me.jinjjahalgae.domain.contract.usecase.get.detail.dto;

 import java.time.LocalDateTime;
 import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import me.jinjjahalgae.domain.contract.enums.ContractStatus;
 import me.jinjjahalgae.domain.contract.enums.ContractType;
 import me.jinjjahalgae.domain.participation.enums.Role;

/**
 * 계약 상세 response
 * 계약서 미리보기 등 여기저기 사용
 */
@Schema(
    title = "계약 상세 응답",
    description = "계약 상세 정보와 서명 정보를 포함한 응답 DTO"
)
 public record ContractDetailResponse(
        @Schema(description = "계약 ID", example = "1")
        Long contractId, //계약 id

        @Schema(description = "계약 UUID", example = "123e4567-e89b-12d3-a456-426614174000")
        String contractUuid, //계약 uuid

        @Schema(description = "계약 제목", example = "매일 운동하기")
        String title, //목표 제목

        @Schema(description = "목표 상세", example = "매일 30분 이상 운동하기")
        String goal, //목표 상세

        @Schema(description = "벌칙", example = "치킨 못 먹기")
        String penalty, //벌칙

        @Schema(description = "보상", example = "치킨 먹기")
        String reward, //보상

        //프리뷰 전용
        @Schema(description = "계약서 디자인 타입", example = "BASIC")
        ContractType type, //계약서 디자인 타입

        //공통
        @Schema(description = "시작 날짜", example = "2024-01-01T09:00:00")
        LocalDateTime startDate, //시작 날짜
        @Schema(description = "종료 날짜", example = "2024-01-31T23:59:59")
        LocalDateTime endDate, //종료 날짜

        //상세 조회 전용
        @Schema(description = "계약 상태", example = "ACTIVE")
        ContractStatus contractStatus, //현재 상태(진행중 등)
        @Schema(description = "한 주 인증 수", example = "3")
        int proofPerWeek, //설정한 한 주 인증 수

        //공통
        @Schema(description = "총 인증 수", example = "10")
        int totalProof, // 총 필요한 인증 수

        //상세 전용
        @Schema(description = "현재 인증 수", example = "5")
        int currentProof, //지금까지 성공한 인증 수

        //공통
        @Schema(description = "원본 실패 가능 횟수 (계약서용)", example = "3")
        int totalLife,

        //상세 페이지 전용
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
        double periodPercent,      // 계산해서 제공

        @Schema(description = "참여자 정보", example = "참여자 정보")
        List<ParticipantSimpleResponse> participants //참여한 유저 정보
 ) {
     public record ParticipantSimpleResponse(
         Long userId,
         String name,
         Role role,
         String signatureImageKey
     ) {}
 }