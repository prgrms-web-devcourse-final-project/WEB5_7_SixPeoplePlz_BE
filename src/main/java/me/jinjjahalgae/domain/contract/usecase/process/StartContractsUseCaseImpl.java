package me.jinjjahalgae.domain.contract.usecase.process;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.notification.enums.NotificationType;
import me.jinjjahalgae.domain.notification.usecase.create.CreateNotificationUseCase;
import me.jinjjahalgae.domain.notification.usecase.create.dto.NotificationCreateRequest;
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
        Instant today = Instant.now();
        List<Contract> pendingContracts = contractRepository.findByStatusAndStartDateOnAndOneOff(ContractStatus.PENDING, today, false);

        if (pendingContracts.isEmpty()) return;

        Map<Boolean, List<Contract>> pendingContractsBySupervisors = pendingContracts.stream()
                .collect(Collectors.partitioningBy(
                        contract -> getJoinedSupervisorsUseCase.execute(contract.getId()) > 0
                ));

        List<Contract> startContracts = pendingContractsBySupervisors.get(true);
        List<Contract> deleteContracts = pendingContractsBySupervisors.get(false);

        // 계약 시작 처리
        for (Contract contract : startContracts) {
            int joinedSupervisors = getJoinedSupervisorsUseCase.execute(contract.getId());
            contract.start(joinedSupervisors);

            eventPublisher.publishEvent(new NotificationEvent(
                            NotificationType.CONTRACT_STARTED,
                            contract.getId(),
                            contract.getUser().getId()
                    )
            );
        }

        // 감독자 부족 계약 전체 삭제
        if (!deleteContracts.isEmpty()) {
            contractRepository.deleteAll(deleteContracts);

            deleteContracts.forEach(contract ->
                    createNotificationUseCase.execute(new NotificationCreateRequest(
                            NotificationType.CONTRACT_AUTO_DELETED,
                            contract.getId(),
                            contract.getUser().getId()
                    ))
            );
        }

        // 처리된 계약의 redis 정보 일괄 삭제
        List<Long> allProcessedIds = pendingContracts.stream().map(Contract::getId).toList();
        bulkdeleteInviteInfoUseCase.execute(allProcessedIds);
    }
}
