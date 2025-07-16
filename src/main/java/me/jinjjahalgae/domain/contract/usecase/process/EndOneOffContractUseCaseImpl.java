package me.jinjjahalgae.domain.contract.usecase.process;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.notification.enums.NotificationType;
import me.jinjjahalgae.domain.notification.usecase.listener.event.NotificationEvent;
import me.jinjjahalgae.domain.proof.enums.ProofStatus;
import me.jinjjahalgae.domain.proof.repository.ProofRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.jinjjahalgae.global.util.UtcDateTimeUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EndOneOffContractUseCaseImpl implements EndOneOffContractUseCase {
    private final ProofRepository proofRepository;
    private final ContractRepository contractRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 단건 계약(oneOff=true)이 시작되고 24시간이 지났는지 확인하고 계약 상태를 결정합니다.
     * - 인증을 올렸는데 계약이 생성된지 24시간이 지난 후에도 피드백이 없으면 계약 성공
     * - 인증을 안올리고 계약이 생성된지 24시간이 지나면 계약 실패
     */
    @Override
    public void execute() {
        LocalDateTime deadline = UtcDateTimeUtil.nowAsLocalDateTime().minusHours(24);

        // 성공 처리 대상 계약들 처리
        List<Contract> completableContracts = contractRepository.findCompletableOneOffContracts(deadline);

        if (!completableContracts.isEmpty()) {
            List<Long> contractIdsToComplete = completableContracts.stream()
                    .map(Contract::getId)
                    .toList();

            // 인증 APPROVED 벌크 업데이트
            proofRepository.bulkUpdatePendingProofsToApproved(
                    contractIdsToComplete,
                    ProofStatus.APPROVED
            );

            // 계약 COMPLETE 벌크 업데이트
            contractRepository.bulkUpdateStatus(contractIdsToComplete, ContractStatus.COMPLETED);

            for (Contract contract : completableContracts) {
                // 계약 성공 알림 이벤트 발행
                eventPublisher.publishEvent(
                    new NotificationEvent(NotificationType.CONTRACT_ENDED_SUCCESS, contract.getId(), contract.getUser().getId())
                );
            }
        }

        // 실패 처리 대상 계약들 처리
        List<Contract> failableContracts = contractRepository.findFailableOneOffContracts(deadline);

        if(!failableContracts.isEmpty()) {
            List<Long> contractIdsToFail = failableContracts.stream()
                    .map(Contract::getId)
                    .toList();

            contractRepository.bulkUpdateStatus(contractIdsToFail, ContractStatus.FAILED);

            for (Contract contract : failableContracts) {
                // 계약 실패 알림 이벤트 발행
                eventPublisher.publishEvent(
                        new NotificationEvent(NotificationType.CONTRACT_ENDED_FAIL, contract.getId(), contract.getUser().getId())
                );
            }
        }

    }
} 