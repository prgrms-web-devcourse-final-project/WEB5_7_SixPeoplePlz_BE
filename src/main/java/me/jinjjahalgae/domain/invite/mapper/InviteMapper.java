package me.jinjjahalgae.domain.invite.mapper;

import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.invite.usecase.verify.password.dto.ContractUuidResponse;
import me.jinjjahalgae.domain.invite.usecase.get.contract.dto.InviteContractInfoResponse;
import me.jinjjahalgae.domain.invite.usecase.create.invite.dto.InviteLinkResponse;
import me.jinjjahalgae.domain.invite.model.SupervisorResponse;
import me.jinjjahalgae.domain.participation.enums.Role;

import java.util.List;

public class InviteMapper {

    /**
     * 초대 링크와 비밀번호로 InviteLinkResponse 생성
     * @param inviteUrl 생성된 초대 링크 URL
     * @param password 생성된 비밀번호
     * @return {@link InviteLinkResponse}
     */
    public static InviteLinkResponse toInviteLinkResponse(String inviteUrl, String password) {
        return new InviteLinkResponse(inviteUrl, password);
    }

    /**
     * 계약 UUID로 ContractUuidResponse 생성
     * @param contractUuid 계약의 고유 UUID
     * @return {@link ContractUuidResponse}
     */
    public static ContractUuidResponse toContractUuidResponse(String contractUuid) {
        return new ContractUuidResponse(contractUuid);
    }

    /**
     * Contract 엔티티를 InviteContractInfoResponse로 변환
     * @param contract 조회된 Contract 엔티티
     * @return {@link InviteContractInfoResponse}
     */
    public static InviteContractInfoResponse toInviteContractInfoResponse(Contract contract) {
        // 감독자 정보 목록 생성 (서명 완료 여부만 보여주니 이미지 키는 null로 처리)
        List<SupervisorResponse> supervisorInfos = contract.getParticipations().stream()
                .filter(p -> p.getRole() == Role.SUPERVISOR)
                .map(p -> new SupervisorResponse(p.getUser().getName(), null))
                .toList();

        return new InviteContractInfoResponse(
                contract.getUser().getName(),
                null, // 서명 완료 여부만 보여주니 이미지 키는 null로 처리
                contract.getUuid(),
                contract.getStartDate(),
                contract.getEndDate(),
                contract.getTitle(),
                contract.getGoal(),
                contract.getPenalty(),
                contract.getReward(),
                contract.getLife(),
                contract.getTotalProof(),
                contract.getTotalSupervisor(),
                contract.isOneOff(),
                contract.getStatus(),
                contract.getType(),
                supervisorInfos
        );
    }
}