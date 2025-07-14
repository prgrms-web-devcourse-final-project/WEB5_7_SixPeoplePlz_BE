package me.jinjjahalgae.domain.contract.scheduler;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.notification.enums.NotificationType;
import me.jinjjahalgae.domain.notification.usecase.create.CreateNotificationUseCase;
import me.jinjjahalgae.domain.notification.usecase.create.dto.NotificationCreateRequest;
import me.jinjjahalgae.global.storage.redis.usecase.invite.delete.DeleteInviteInfoUseCase;
import me.jinjjahalgae.global.storage.redis.usecase.invite.get.GetJoinedSupervisorsUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ContractScheduler {

    private final ContractRepository contractRepository;
    private final DeleteInviteInfoUseCase deleteInviteInfoUseCase;
    private final CreateNotificationUseCase createNotificationUseCase;
    private final GetJoinedSupervisorsUseCase getJoinedSupervisorsUseCase;

    // 시작일로 대기중인 기본계약(oneOff=false) 목록 조회
    @Scheduled(cron = "0 1 0 * * *")
    @Transactional
    public void startContracts() {
        LocalDate today = LocalDate.now();
        List<Contract> pendingContracts = contractRepository.findByStatusAndStartDateOnAndOneOff(ContractStatus.PENDING, today, false);

        if (pendingContracts.isEmpty()) {
            return;
        }

        for (Contract contract : pendingContracts) {
            int joinedSupervisors = getJoinedSupervisorsUseCase.execute(contract.getId());

            if (joinedSupervisors > 0) {
                contract.start(joinedSupervisors);
                createNotificationUseCase.execute(new NotificationCreateRequest(NotificationType.CONTRACT_STARTED, contract.getId(), contract.getUser().getId()));
            } else {
                contractRepository.delete(contract);
                createNotificationUseCase.execute(new NotificationCreateRequest(NotificationType.CONTRACT_AUTO_DELETED, contract.getId(), contract.getUser().getId()));
            }

            deleteInviteInfoUseCase.execute(contract.getId());
        }
    }

    // 종료일로 진행중인 기본계약(oneOff=false) 목록 조회
    @Scheduled(cron = "0 59 23 * * *")
    @Transactional
    public void endContracts() {
        LocalDate today = LocalDate.now();
        
        List<Contract> progressingContracts = contractRepository.findByStatusAndEndDateOnAndOneOff(ContractStatus.IN_PROGRESS, today, false);

        if (progressingContracts.isEmpty()) {
            return;
        }

        for (Contract contract : progressingContracts) {
            if (contract.getLife() < contract.getCurrentFail()){
                contract.fail();
                createNotificationUseCase.execute(new NotificationCreateRequest(NotificationType.CONTRACT_ENDED_FAIL, contract.getId(), contract.getUser().getId()));
            } else {
                contract.complete();
                createNotificationUseCase.execute(new NotificationCreateRequest(NotificationType.CONTRACT_ENDED_SUCCESS, contract.getId(), contract.getUser().getId()));
            }
        }
    }
}
