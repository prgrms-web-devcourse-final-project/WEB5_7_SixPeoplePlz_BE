package me.jinjjahalgae.domain.invite.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import me.jinjjahalgae.domain.contract.enums.ContractType;
import me.jinjjahalgae.domain.invite.model.SupervisorInfo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 초대링크 비밀번호 입력 후 계약서 response
 * @param contractorName            계약자 이름
 * @param contractorSignatureKey    계약자 서명 이미지 키
 * @param uuid                      계약 고유 uuid
 * @param startDate                 계약 시작일
 * @param endDate                   계약 종료일
 * @param title                     목표 제목
 * @param goal                      계약 목표
 * @param penalty                   벌칙
 * @param reward                    보상
 * @param life                      실패 가능 횟수
 * @param totalProof                총 인증 횟수
 * @param totalSupervisor           현재 감독자 수
 * @param oneOff                    단발성 여부
 * @param status                    계약 상태
 * @param type                      계약서 템플릿 타입
 * @param supervisorInfos           감독자 이름과 서명 이미지 키 목록
 */
@Schema(
        title = "계약서 미리보기 응답",
        description = "초대 링크를 통해 조회한 계약 정보 DTO",
        example = """
        {
          "contractorName": "홍길동",
          "contractorSignatureKey": "36865103-5d08-4139-ba4a-b32da2316d7f",
          "uuid": "36865103-5d08-4139-ba4a-b32da2316d7f",
          "startDate": "2025-07-01T00:00:00",
          "endDate": "2025-07-31T23:59:59",
          "title": "매일 아침 30분 운동하기",
          "goal": "한 달 동안 매일 아침 조깅을 하여 체력을 증진한다.",
          "penalty": "실패 시 친구에게 커피 사주기",
          "reward": "성공 시 나에게 선물 사주기",
          "life": 3,
          "totalProof": 30,
          "totalSupervisor": 2,
          "oneOff": false,
          "status": "PENDING",
          "type": "BASIC",
          "supervisorInfos": [
            {
              "supervisorName": "김감독",
              "supervisorSignatureKey": "1a2b3c4d-5e6f-7g8h-9i0j-1k2l3m4n5o6p"
            },
            {
              "supervisorName": "박감시",
              "supervisorSignatureKey": "9z8y7x6w-5v4u-3t2s-1r0q-p9o8n7m6l5k4"
            }
          ]
        }
        """
)
public record InviteContractInfoResponse(
        @Schema(description = "계약자 이름")
        String contractorName,

        @Schema(description = "계약자 서명 이미지 키")
        String contractorSignatureKey,

        @Schema(description = "계약 고유 uuid")
        String uuid,

        @Schema(description = "계약 시작일")
        LocalDateTime startDate,

        @Schema(description = "계약 종료일")
        LocalDateTime endDate,

        @Schema(description = "목표 제목")
        String title,

        @Schema(description = "계약 목표")
        String goal,

        @Schema(description = "벌칙")
        String penalty,

        @Schema(description = "보상")
        String reward,

        @Schema(description = "실패 가능 횟수")
        int life,

        @Schema(description = "총 인증 횟수")
        int totalProof,

        @Schema(description = "현재 감독자 수")
        int totalSupervisor,

        @Schema(description = "단발성 여부")
        boolean oneOff,

        @Schema(description = "계약 상태")
        ContractStatus status,

        @Schema(description = "계약서 템플릿 타입")
        ContractType type,

        @Schema(description = "감독자 정보 목록 (이름과 서명 이미지 키)")
        List<SupervisorInfo> supervisorInfos
) {
}