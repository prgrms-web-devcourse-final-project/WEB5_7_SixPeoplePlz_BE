package me.jinjjahalgae.domain.contract.usecase.process;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.notification.enums.NotificationType;
import me.jinjjahalgae.domain.notification.usecase.listener.event.NotificationEvent;
import me.jinjjahalgae.domain.proof.enums.ProofStatus;
import me.jinjjahalgae.domain.proof.repository.ProofRepository;
import me.jinjjahalgae.global.storage.redis.usecase.invite.bulk.BulkDeleteInviteInfoUseCase;
import me.jinjjahalgae.global.storage.redis.usecase.invite.get.GetJoinedSupervisorsUseCase;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EndContractsUseCaseImpl implements EndContractsUseCase {

    private final ContractRepository contractRepository;
    private final ProofRepository proofRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void execute() {
        // 대기중인 인증이 "없는" 단발이 아닌 계약을 결과 대기중 상태로 변환
        LocalDate yesterday = LocalDate.now().minusDays(1);
        contractRepository.bulkUpdateCompletedContractsToWait(yesterday);

        List<Contract> waitingContracts = contractRepository.findByStatus(ContractStatus.WAIT_RESULT);

        if (waitingContracts.isEmpty()) {
            // 대기중인 인증이 "있는" 단발이 아닌 계약을 결과 대기중 상태로 변환
            contractRepository.bulkUpdateApprovePendingContractsToWait(yesterday);

            return;
        }

        // 마지막 주차 실패 횟수 업데이트
        for (Contract contract : waitingContracts) {
            long totalDays = ChronoUnit.DAYS.between(contract.getStartDate().toLocalDate(), contract.getEndDate().toLocalDate()) + 1;

            if (totalDays % 7 > 0) { // 마지막 주가 7일 미만일 경우
                // 마지막 주차 시작일 계산
                long totalWeeks = totalDays / 7;
                LocalDateTime startOfLastWeek = contract.getStartDate().toLocalDate().plusDays(totalWeeks * 7).atStartOfDay();

                // 마지막 주차의 인증 승인 횟수 계산
                int successCount = proofRepository.countByContractIdAndStatusAndCreatedAtBetween(
                        contract.getId(), ProofStatus.APPROVED, startOfLastWeek, contract.getEndDate());
                if (successCount < contract.getProofPerWeek()) {
                    contract.recordWeeklyFailure(contract.getProofPerWeek() - successCount);
                }
            }
        }

        Map<Boolean, List<Contract>> partitionedContractsByResult = waitingContracts.stream()
                .collect(Collectors.partitioningBy(
                        contract -> contract.getLife() >= contract.getCurrentFail()
                ));

        List<Contract> successContracts = partitionedContractsByResult.get(true);
        List<Contract> failContracts = partitionedContractsByResult.get(false);

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

        // 대기중인 인증이 "있는" 단발이 아닌 계약을 결과 대기중 상태로 변환
        contractRepository.bulkUpdateApprovePendingContractsToWait(yesterday);
    }
}