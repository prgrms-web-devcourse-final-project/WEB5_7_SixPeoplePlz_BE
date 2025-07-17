package me.jinjjahalgae.domain.contract.usecase.process;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.notification.enums.NotificationType;
import me.jinjjahalgae.domain.notification.model.NotificationData;
import me.jinjjahalgae.domain.notification.usecase.listener.event.NotificationBatchEvent;
import me.jinjjahalgae.domain.notification.usecase.listener.event.NotificationEvent;
import me.jinjjahalgae.domain.proof.enums.ProofStatus;
import me.jinjjahalgae.domain.proof.repository.ProofRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class EndContractsUseCaseImpl implements EndContractsUseCase {

    private final ContractRepository contractRepository;
    private final ProofRepository proofRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void execute() {
        Instant today = Instant.now();
        // 어제 또는 이전에 종료되었어야 하는 '진행중' 또는 '결과 대기' 상태의 계약을 모두 조회
        List<Contract> contractsToCheck = contractRepository.findContractsToEnd(
                List.of(ContractStatus.IN_PROGRESS, ContractStatus.WAIT_RESULT), today
        );

        if (contractsToCheck.isEmpty()) return;

        List<Contract> completeContracts = new ArrayList<>();
        List<Contract> waitContracts = new ArrayList<>();
        List<Contract> failContracts = new ArrayList<>();

        Instant twentyFourHours = Instant.now().minusSeconds(24 * 3600);

        for (Contract contract : contractsToCheck) {
            boolean hasPendingProofs = proofRepository.existsByContractIdAndStatus(contract.getId(), ProofStatus.APPROVE_PENDING);

            // 성공 확정 (이미 목표를 달성했고, 처리 대기중인 인증도 없는 경우)
            if (contract.getCurrentProof() >= contract.getTotalProof() && !hasPendingProofs) {
                completeContracts.add(contract);
                continue;
            }

            // 실패 확정 (목표 미달성, 재인증 가능성 없음, 처리 대기 인증도 없는 경우)
            int reProofableCount = proofRepository.countRecentRejectedProofs(contract.getId(), twentyFourHours);
            if (contract.getCurrentProof() + reProofableCount < contract.getTotalProof() && !hasPendingProofs) {
                failContracts.add(contract);
                continue;
            }

            // 결과가 애매한 경우 'WAIT_RESULT' 상태로 변경해 하루 더 유예
            waitContracts.add(contract);
        }

        // 성공 계약 일괄 업데이트 및 batch 알림
        if (!completeContracts.isEmpty()) {
            List<Long> successIds = completeContracts.stream().map(Contract::getId).toList();
            contractRepository.bulkUpdateStatus(successIds, ContractStatus.COMPLETED);
            createBatchNotificationsToParticipants(completeContracts, NotificationType.CONTRACT_ENDED_SUCCESS);
        }

        // 결과 대기 계약 일괄 업데이트
        if (!waitContracts.isEmpty()) {
            List<Long> waitIds = waitContracts.stream().map(Contract::getId).toList();
            contractRepository.bulkUpdateStatus(waitIds, ContractStatus.WAIT_RESULT);
        }

        // 실패 계약 일괄 업데이트 및 batch 알림
        if (!failContracts.isEmpty()) {
            List<Long> failIds = failContracts.stream().map(Contract::getId).toList();
            contractRepository.bulkUpdateStatus(failIds, ContractStatus.FAILED);
            createBatchNotificationsToParticipants(failContracts, NotificationType.CONTRACT_ENDED_FAIL);
        }
    }

    private void createBatchNotificationsToParticipants(List<Contract> contracts, NotificationType type) {
        List<NotificationData> notificationDataList = contracts.stream()
                .map(contract -> new NotificationData(contract.getId(), contract.getUser().getId()))
                .toList();

        eventPublisher.publishEvent(new NotificationBatchEvent(type, notificationDataList));
    }
}