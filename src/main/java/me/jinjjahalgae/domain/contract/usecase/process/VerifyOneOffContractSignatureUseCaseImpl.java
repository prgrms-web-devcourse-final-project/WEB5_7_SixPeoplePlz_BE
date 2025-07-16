package me.jinjjahalgae.domain.contract.usecase.process;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.notification.enums.NotificationType;
import me.jinjjahalgae.domain.notification.usecase.listener.event.NotificationEvent;
import me.jinjjahalgae.global.storage.redis.usecase.invite.delete.DeleteInviteInfoUseCase;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.jinjjahalgae.global.util.UtcDateTimeUtil;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class VerifyOneOffContractSignatureUseCaseImpl implements VerifyOneOffContractSignatureUseCase {

    private final ContractRepository contractRepository;
    private final DeleteInviteInfoUseCase deleteInviteInfoUseCase;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 단건 계약(oneOff=true)의 서명을 검증합니다
     * 계약이 생성된 후 24시간 내에 1명이라도 감독자가 서명하지 않은 계약은 삭제됩니다.
     */
    @Override
    public void execute() {
        // 24시간 전을 마감 시간으로 설정
        Instant deadline = Instant.now().minus(24, ChronoUnit.HOURS);

        log.info("[VerifyOneOffContract] 계약 만료 검증 시작. 기준 시간(UTC): {}", deadline);


        // 감독자 서명안된 단건 계약 조회
        List<Contract> contractsToDelete = contractRepository.findExpiredOneOffContractsWithNoSupervisors(deadline);

        log.info("[VerifyOneOffContract] 삭제 대상 계약 {}건 발견", contractsToDelete.size());


        for (Contract contract : contractsToDelete) {
            // 조회된 모든 감독자 서명안된 단건 계약을 삭제
            contractRepository.delete(contract);

            log.info("[VerifyOneOffContract] 계약 삭제 처리. Contract ID: {}", contract.getId());


            // 자동 삭제 알림 이벤트 발행
            eventPublisher.publishEvent(
                new NotificationEvent(NotificationType.CONTRACT_AUTO_DELETED, contract.getId(), contract.getUser().getId())
            );

            // 초대 정보 삭제
            deleteInviteInfoUseCase.execute(contract.getId());
        }
    }
} 