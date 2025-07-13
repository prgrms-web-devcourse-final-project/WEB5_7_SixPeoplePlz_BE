package me.jinjjahalgae.domain.contract.scheduler;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.notification.enums.NotificationType;
import me.jinjjahalgae.global.storage.redis.usecase.invite.bulk.BulkDeleteInviteInfoUseCase;
import me.jinjjahalgae.global.storage.redis.usecase.invite.get.GetJoinedSupervisorsUseCase;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ContractScheduler {

    private final ContractRepository contractRepository;
    private final BulkDeleteInviteInfoUseCase bulkdeleteInviteInfoUseCase;
    private final GetJoinedSupervisorsUseCase getJoinedSupervisorsUseCase;
    private final ApplicationEventPublisher eventPublisher;

    @Scheduled(cron = "0 1 0 * * *")
    @Transactional
    public void startContracts() {
        LocalDate today = LocalDate.now();
        List<Contract> pendingContracts = contractRepository.findByStatusAndStartDateOn(ContractStatus.PENDING, today);

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

        // 감독자 부족 계약 벌크 삭제
        if (!deleteContracts.isEmpty()) {
            List<Long> deleteContractIds = deleteContracts.stream().map(Contract::getId).toList();
            contractRepository.deleteAllByIdInBatch(deleteContractIds);
            
            deleteContracts.forEach(contract ->
                    eventPublisher.publishEvent(new NotificationEvent(
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

    @Scheduled(cron = "0 59 23 * * *")
    @Transactional
    public void endContracts() {
        LocalDate today = LocalDate.now();
        List<Contract> progressingContracts = contractRepository.findByStatusAndEndDateOn(ContractStatus.IN_PROGRESS, today);

        if (progressingContracts.isEmpty()) return;

        Map<Boolean, List<Contract>> partitionedContractsByProof = progressingContracts.stream()
                .collect(Collectors.partitioningBy(
                        contract -> contract.getCurrentProof() >= contract.getTotalProof()
                ));

        List<Contract> successContracts = partitionedContractsByProof.get(true);
        List<Contract> failContracts = partitionedContractsByProof.get(false);

        // 성공 계약 벌크 업데이트
        if (!successContracts.isEmpty()) {
            List<Long> successIds = successContracts.stream().map(Contract::getId).toList();
            contractRepository.bulkUpdateStatus(successIds, ContractStatus.COMPLETED);

            successContracts.forEach(contract ->
                    eventPublisher.publishEvent(new NotificationEvent(
                            NotificationType.CONTRACT_ENDED_SUCCESS,
                            contract.getId(),
                            contract.getUser().getId()
                    ))
            );
        }

        // 실패 계약 벌크 업데이트
        if (!failContracts.isEmpty()) {
            List<Long> failIds = failContracts.stream().map(Contract::getId).toList();
            contractRepository.bulkUpdateStatus(failIds, ContractStatus.FAILED);

            failContracts.forEach(contract ->
                    eventPublisher.publishEvent(new NotificationEvent(
                            NotificationType.CONTRACT_ENDED_FAIL,
                            contract.getId(),
                            contract.getUser().getId()
                    ))
            );
        }
    }
}
