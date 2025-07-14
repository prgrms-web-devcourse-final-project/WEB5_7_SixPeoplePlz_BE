package me.jinjjahalgae.domain.contract.usecase.process;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.notification.enums.NotificationType;
import me.jinjjahalgae.domain.notification.usecase.listener.event.NotificationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EndOneOffContractUseCaseImpl implements EndOneOffContractUseCase {

    private final ContractRepository contractRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 단건 계약(oneOff=true)이 시작되고 24시간이 지났는지 확인하고 계약 상태를 결정합니다.
     * - 인증을 올렸는데 계약이 생성된지 24시간이 지난 후에도 피드백이 없으면 계약 성공
     * - 인증을 안올리고 계약이 생성된지 24시간이 지나면 계약 실패
     */
    @Override
    public void execute() {
        LocalDateTime deadline = LocalDateTime.now().minusHours(24);

        // 성공 처리 대상 계약들 처리
        List<Contract> completableContracts = contractRepository.findCompletableOneOffContracts(deadline);

        for (Contract contract : completableContracts) {
            contract.complete();

            // 계약 성공 알림 이벤트 발행
            eventPublisher.publishEvent(
                new NotificationEvent(NotificationType.CONTRACT_ENDED_SUCCESS, contract.getId(), contract.getUser().getId())
            );
        }

        // 실패 처리 대상 계약들 처리
        List<Contract> failableContracts = contractRepository.findFailableOneOffContracts(deadline);

        for (Contract contract : failableContracts) {
            contract.fail();

            // 계약 실패 알림 이벤트 발행
            eventPublisher.publishEvent(
                new NotificationEvent(NotificationType.CONTRACT_ENDED_FAIL, contract.getId(), contract.getUser().getId())
            );
        }
    }
} 