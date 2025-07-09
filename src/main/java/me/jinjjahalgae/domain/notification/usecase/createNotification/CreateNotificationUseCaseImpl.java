package me.jinjjahalgae.domain.notification.usecase.createNotification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.notification.entities.Notification;
import me.jinjjahalgae.domain.notification.enums.NotificationType;
import me.jinjjahalgae.domain.notification.repository.NotificationRepository;
import me.jinjjahalgae.domain.participation.dto.response.ParticipantInfoResponse;
import me.jinjjahalgae.domain.participation.enums.Role;
import me.jinjjahalgae.domain.participation.usecase.interfaces.GetParticipantInfoByContractIdUseCase;
import me.jinjjahalgae.domain.user.usecase.interfaces.GetMyInfoUseCase;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateNotificationUseCaseImpl implements CreateNotificationUseCase {

    private final NotificationRepository notificationRepository;
    private final ContractRepository contractRepository;

    // Impl이 아닌 인터페이스를 타입으로 가져옴
    private final GetParticipantInfoByContractIdUseCase getParticipantInfoByContractId;
    private final GetMyInfoUseCase getMyInfo;

    @Transactional // 트랜잭션 생성
    @Override
    public void execute(NotificationCreateRequest request) {

        // 불필요한 객체 생성 방지. 재사용하기.
        Notification notification;
        String message;

        // 계약id로 관련유저 정보리스트에서 필요한 것만 필터링한 결과를 저장할 리스트
        List<ParticipantInfoResponse> targetUserList;

        // DB에 저장할 알림 모음 리스트
        List<Notification> notificationList = new ArrayList<>();

        // userid로 이름 가져오기
        String actionUserName = getMyInfo.execute(request.actorUserId()).name();

        // 메세지에 들어갈 계약 제목 찾아오기
        Contract contract = contractRepository.findContractById(request.contractId())
                .orElseThrow(() -> ErrorCode.CONTRACT_NOT_FOUND.serviceException("계약 ID에 맞는 계약을 찾지 못했습니다 : " + request.contractId()));
        String contractName = contract.getTitle();

        // 계약id와 관련 있는 유저들의 정보 모음 (이름, id, role)
        List<ParticipantInfoResponse> participantInfoList = getParticipantInfoByContractId.execute(request.contractId());

        // 알림 타입에 따라 알림 보낼 대상 리스트, 메세지를 다르게 설정
        switch (NotificationType.valueOf(request.type())) {
            // 감독자 추가됨 (to 계약자)
            case SUPERVISOR_ADDED -> {
                targetUserList = getContractorInfoList(participantInfoList);
                message = "'" + actionUserName + "'님이 '" + contractName + "' 계약의 감독자로 추가되었습니다.";
            }

            // 감독자 포기함 (to 계약자)
            case SUPERVISOR_WITHDRAWN -> {
                targetUserList = getContractorInfoList(participantInfoList);
                message = "'" + actionUserName + "'님이 '" + contractName + "' 계약을 더이상 감독하지 않습니다.";
            }

            // 계약 시작됨 (to 계약자, 감독자들)
            case CONTRACT_STARTED -> {
                targetUserList = participantInfoList;
                message = "'" + actionUserName + "'님의 '" + contractName + "' 계약이 시작되었습니다.";
            }

            // 계약 실패 상태로 종료됨 (to 계약자, 감독자들)
            case CONTRACT_ENDED_FAIL -> {
                targetUserList = participantInfoList;
                message = "'" + actionUserName + "'님의 '" + contractName + "' 계약이 실패로 종료되었습니다.";
            }

            // 계약 성공 상태로 종료됨 (to 계약자, 감독자들)
            case CONTRACT_ENDED_SUCCESS -> {
                targetUserList = participantInfoList;
                message = "'" + actionUserName + "'님의 '" + contractName + "' 계약이 성공으로 종료되었습니다.";
            }

            // 인증 등록됨 (to 감독자들)
            case PROOF_ADDED -> {
                targetUserList = getSupervisorInfoList(participantInfoList);
                message = "'" + actionUserName + "'님이 '" + contractName + "' 계약의 인증을 등록하였습니다.";
            }

            // 인증 승인됨 (to 계약자)
            case PROOF_ACCEPTED -> {
                targetUserList = getContractorInfoList(participantInfoList);
                message = contractName + " 계약의 인증이 최종 승인되었습니다.";
            }

            // 인증 거절됨 (to 계약자)
            case PROOF_REJECTED -> {
                targetUserList = getContractorInfoList(participantInfoList);
                message = contractName + " 계약의 인증 승인이 최종 거절되었습니다.";
            }

            // 피드백 등록됨 (to 계약자)
            case FEEDBACK_ADDED -> {
                targetUserList = getContractorInfoList(participantInfoList);
                message = "'" + actionUserName + "'님이 '" + contractName + "' 계약의 인증을 확인하였습니다.";
            }

            // 재인증 등록됨 (to 모든 감독자들)
            case REPROOF_ADDED -> {
                targetUserList = getSupervisorInfoList(participantInfoList);
                message = "'" + actionUserName + "'님이 '" + contractName + "' 계약의 재인증을 등록하였습니다.";
            }

            default -> {
                throw ErrorCode.INVALID_NOTIFICATION_TYPE.serviceException("지원하지 않는 알림 타입입니다 : " + request.type());
            }
        }

        // 모든 알림 타겟 유저id들에 대해 알림 생성 및 저장

        for (ParticipantInfoResponse target : targetUserList) {
            notification = Notification.builder()
                    .userId(target.userId()) // 메세지를 보낼 대상
                    .contractId(request.contractId())
                    .content(message)
                    .type(NotificationType.valueOf(request.type()))
                    .build();

            notificationList.add(notification);
        }

        // saveAll로 DB 접근횟수과 트랜잭션 수 줄이기
        notificationRepository.saveAll(notificationList);

        log.info("생성된 알림 수 : " + notificationList.size());
        log.info("생성된 메세지 : " + message);
    }

    /// 계약과 관련된 유저 중 유효한 감독자만 필터링 해서 반환
    private static List<ParticipantInfoResponse> getSupervisorInfoList(List<ParticipantInfoResponse> participantInfoList) {
        return participantInfoList.stream()
                .filter(info -> info.role() == Role.SUPERVISOR)
                .filter(ParticipantInfoResponse::valid)
                .toList();
    }

    /// 계약과 관련된 유저 중 유효한 계약자만 필터링 해서 반환 (계약자는 1명만 존재)
    private static List<ParticipantInfoResponse> getContractorInfoList(List<ParticipantInfoResponse> participantInfoList) {
        return participantInfoList.stream()
                .filter(info -> info.role() == Role.CONTRACTOR)
                .filter(ParticipantInfoResponse::valid)
                .toList();
    }
}
