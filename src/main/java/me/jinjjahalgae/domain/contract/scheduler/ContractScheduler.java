package me.jinjjahalgae.domain.contract.scheduler;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.notification.usecase.create.CreateNotificationUseCase;
import me.jinjjahalgae.domain.notification.usecase.create.dto.NotificationCreateRequest;
import me.jinjjahalgae.global.storage.redis.usecase.invite.delete.DeleteInviteInfoUseCase;
import me.jinjjahalgae.global.storage.redis.usecase.invite.get.GetJoinedSupervisorsUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
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

    @Scheduled(cron = "0 1 0 * * *")
    @Transactional
    public void startContracts() {
        // 시작일로 대기중 계약 목록 조회
        LocalDate today = LocalDate.now();
        List<Contract> pendingContracts = contractRepository.findByStatusAndStartDateOn(ContractStatus.PENDING, today);

        if (pendingContracts.isEmpty()) {
            return;
        }

        for (Contract contract : pendingContracts) {
            int joinedSupervisors = getJoinedSupervisorsUseCase.execute(contract.getId());

            if (joinedSupervisors > 0) {
                contract.start(joinedSupervisors);
                createNotificationUseCase.execute(new NotificationCreateRequest("CONTRACT_STARTED", contract.getId(), contract.getUser().getId()));
            } else {
                contractRepository.delete(contract);
                createNotificationUseCase.execute(new NotificationCreateRequest("CONTRACT_AUTO_DELETED", contract.getId(), contract.getUser().getId()));
            }

            deleteInviteInfoUseCase.execute(contract.getId());
        }
    }

    @Scheduled(cron = "0 59 23 * * *")
    @Transactional
    public void endContracts() {
        // 종료일로 진행중 계약 목록 조회
        LocalDate today = LocalDate.now();
        List<Contract> progressingContracts = contractRepository.findByStatusAndEndDateOn(ContractStatus.IN_PROGRESS, today);

        if (progressingContracts.isEmpty()) {
            return;
        }

        for (Contract contract : progressingContracts) {
            if (contract.getLife() < contract.getCurrentFail()){
                contract.fail();
                createNotificationUseCase.execute(new NotificationCreateRequest("CONTRACT_ENDED_FAIL", contract.getId(), contract.getUser().getId()));
            } else {
                contract.complete();
                createNotificationUseCase.execute(new NotificationCreateRequest("CONTRACT_ENDED_SUCCESS", contract.getId(), contract.getUser().getId()));
            }
        }
    }
}
