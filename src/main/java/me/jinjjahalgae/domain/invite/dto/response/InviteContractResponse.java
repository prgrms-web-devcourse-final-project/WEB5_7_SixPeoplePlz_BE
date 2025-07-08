package me.jinjjahalgae.domain.invite.dto.response;

import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import me.jinjjahalgae.domain.contract.enums.ContractType;
import me.jinjjahalgae.domain.invite.dto.SupervisorInfo;

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
 * @param supervisorInfos           감독자 이름과 서명 이미지 키 배열
 */
public record InviteContractResponse(
        String contractorName,
        String contractorSignatureKey,
        String uuid,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String title,
        String goal,
        String penalty,
        String reward,
        int life,
        int totalProof,
        int totalSupervisor,
        boolean oneOff,
        ContractStatus status,
        ContractType type,
        List<SupervisorInfo> supervisorInfos
) {
}
