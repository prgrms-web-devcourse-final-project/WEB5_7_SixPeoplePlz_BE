package me.jinjjahalgae.domain.contract.usecase.process;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.notification.enums.NotificationType;
import me.jinjjahalgae.domain.notification.usecase.listener.event.NotificationBatchEvent;
import me.jinjjahalgae.domain.notification.model.NotificationData;
import me.jinjjahalgae.domain.proof.enums.ProofStatus;
import me.jinjjahalgae.domain.proof.repository.ProofRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EndOneOffContractUseCaseImpl implements EndOneOffContractUseCase {
    private final ProofRepository proofRepository;
    private final ContractRepository contractRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 단건 계약(oneOff=true)이 시작되고 24시간이 지났는지 확인하고 계약 상태를 결정합니다.
     * - 인증을 올렸는데 계약이 생성된지 24시간이 지난 후에도 피드백이 없으면 결과대기 상태로 변경
     * - 인증을 안올리고 계약이 생성된지 24시간이 지나면 계약 실패
     * - 결과대기 상태에서 인증이 생성된지 24시간이 지나면 결과 승인처리
     */
    @Override
    public void execute() {
        Instant deadline = Instant.now().minus(24, ChronoUnit.HOURS);

        List<Contract> endedOneOffContracts = contractRepository.findEndedOneOffContracts(deadline);

        if (endedOneOffContracts.isEmpty()) {
            return;
        }

        List<Contract> completeContracts = new ArrayList<>();
        List<Contract> waitContracts = new ArrayList<>();
        List<Contract> failContracts = new ArrayList<>();

        for (Contract contract : endedOneOffContracts) {
            boolean hasPendingProofs = proofRepository.existsByContractIdAndStatus(contract.getId(), ProofStatus.APPROVE_PENDING);

            // 성공 확정: 목표 달성했고, 처리 대기중인 인증도 없는 경우
            if (contract.getCurrentProof() >= contract.getTotalProof() && !hasPendingProofs) {
                completeContracts.add(contract);

                continue;
            }

            // 실패 확정: 목표 미달성, 처리 대기 인증도 없는 경우
            if (contract.getCurrentProof() < contract.getTotalProof() && !hasPendingProofs) {
                failContracts.add(contract);

                continue;
            }

            // 위 두 조건에 해당하지 않으면 'WAIT_RESULT' 상태로 최대 하루 더 유예
            waitContracts.add(contract);
        }


        // 각 리스트에 따라 일괄 업데이트 및 알림 처리
        if (!completeContracts.isEmpty()) {
            List<Long> successIds = extractIdsFromContracts(completeContracts);

            contractRepository.bulkUpdateStatus(successIds, ContractStatus.COMPLETED);

            createBatchNotificationsToParticipants(completeContracts, NotificationType.CONTRACT_ENDED_SUCCESS);
        }

        if (!waitContracts.isEmpty()) {
            List<Long> waitIds = extractIdsFromContracts(waitContracts);

            contractRepository.bulkUpdateStatus(waitIds, ContractStatus.WAIT_RESULT);
        }

        if (!failContracts.isEmpty()) {
            List<Long> failIds = extractIdsFromContracts(failContracts);

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

    private List<Long> extractIdsFromContracts(List<Contract> contracts) {
        return contracts.stream().map(Contract::getId).toList();
    }
} 