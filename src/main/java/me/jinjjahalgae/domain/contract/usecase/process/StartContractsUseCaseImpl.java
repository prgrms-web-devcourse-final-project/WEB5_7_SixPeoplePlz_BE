package me.jinjjahalgae.domain.contract.usecase.process;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.notification.enums.NotificationType;
import me.jinjjahalgae.domain.notification.model.NotificationData;
import me.jinjjahalgae.domain.notification.usecase.create.CreateNotificationUseCase;
import me.jinjjahalgae.domain.notification.usecase.create.dto.NotificationCreateRequest;
import me.jinjjahalgae.domain.notification.usecase.listener.event.NotificationBatchEvent;
import me.jinjjahalgae.domain.notification.usecase.listener.event.NotificationEvent;
import me.jinjjahalgae.global.storage.redis.usecase.invite.bulk.BulkDeleteInviteInfoUseCase;
import me.jinjjahalgae.global.storage.redis.usecase.invite.get.GetJoinedSupervisorsUseCase;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StartContractsUseCaseImpl implements StartContractsUseCase {

    private final ContractRepository contractRepository;
    private final BulkDeleteInviteInfoUseCase bulkdeleteInviteInfoUseCase;
    private final GetJoinedSupervisorsUseCase getJoinedSupervisorsUseCase;
    private final ApplicationEventPublisher eventPublisher;
    private final CreateNotificationUseCase createNotificationUseCase;

    @Override
    @Transactional
    public void execute() {
        log.info("계약 시작 처리를 시작합니다.");
        Instant now = Instant.now();
        List<Contract> pendingContracts = contractRepository.findPendingContractsToStart(now);
        log.info("시작일이 된 계약 {}건을 발견했습니다.", pendingContracts.size());

        if (pendingContracts.isEmpty()) return;

        Map<Boolean, List<Contract>> pendingContractsBySupervisors = pendingContracts.stream()
                .collect(Collectors.partitioningBy(
                        contract -> getJoinedSupervisorsUseCase.execute(contract.getId()) > 0
                ));

        List<Contract> startContracts = pendingContractsBySupervisors.get(true);
        List<Contract> deleteContracts = pendingContractsBySupervisors.get(false);

        log.info("시작 계약 분류 결과: [시작 대상: {}건], [삭제 대상: {}건]", startContracts.size(), deleteContracts.size());

        // 계약 시작 처리 및 batch 알림
        if (!startContracts.isEmpty()) {
            log.info("{}건의 계약을 '진행중' 상태로 변경합니다. Contract IDs: {}", startContracts.size(), startContracts.stream().map(Contract::getId).toList());

            for (Contract contract : startContracts) {
                int joinedSupervisors = getJoinedSupervisorsUseCase.execute(contract.getId());
                contract.start(joinedSupervisors);
            }
            createBatchNotifications(startContracts, NotificationType.CONTRACT_STARTED);
        }

        // 감독자 부족 계약 전체 삭제
        if (!deleteContracts.isEmpty()) {
            log.info("감독자 미참여로 {}건의 계약을 삭제합니다. Contract IDs: {}", deleteContracts.size(), deleteContracts.stream().map(Contract::getId).toList());

            // 트랜잭션 분리로 usecase를 불러와 알림 전송
            deleteContracts.forEach(contract ->
                    createNotificationUseCase.execute(new NotificationCreateRequest(
                            NotificationType.CONTRACT_AUTO_DELETED,
                            contract.getId(),
                            contract.getUser().getId()
                    ))
            );

            contractRepository.deleteAll(deleteContracts);
        }

        // 처리된 계약의 redis 정보 일괄 삭제
        List<Long> allProcessedIds = pendingContracts.stream().map(Contract::getId).toList();
        bulkdeleteInviteInfoUseCase.execute(allProcessedIds);

        log.info("계약 시작 처리를 완료했습니다.");
    }

    private void createBatchNotifications(List<Contract> contracts, NotificationType type) {
        if (contracts == null || contracts.isEmpty()) {
            return;
        }

        List<NotificationData> notificationDataList = contracts.stream()
                .map(contract -> new NotificationData(contract.getId(), contract.getUser().getId()))
                .toList();

        eventPublisher.publishEvent(new NotificationBatchEvent(type, notificationDataList));
    }
}
