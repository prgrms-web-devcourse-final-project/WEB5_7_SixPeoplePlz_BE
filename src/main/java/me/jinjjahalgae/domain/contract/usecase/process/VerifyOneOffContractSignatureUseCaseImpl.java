package me.jinjjahalgae.domain.contract.usecase.process;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.notification.enums.NotificationType;
import me.jinjjahalgae.domain.notification.usecase.create.CreateNotificationUseCase;
import me.jinjjahalgae.domain.notification.usecase.create.dto.NotificationCreateRequest;
import me.jinjjahalgae.global.storage.redis.usecase.invite.delete.DeleteInviteInfoUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VerifyOneOffContractSignatureUseCaseImpl implements VerifyOneOffContractSignatureUseCase {

    private final ContractRepository contractRepository;
    private final DeleteInviteInfoUseCase deleteInviteInfoUseCase;
    private final CreateNotificationUseCase createNotificationUseCase;

    /**
     * 단건 계약(oneOff=true)의 서명을 검증합니다
     * 계약이 생성된 후 24시간 내에 1명이라도 감독자가 서명하지 않은 계약은 삭제됩니다.
     */
    @Override
    public void execute() {
        // 24시간 전을 마감 시간으로 설정
        LocalDateTime deadline = LocalDateTime.now().minusHours(24);

        // 감독자 서명안된 단건 계약 조회
        List<Contract> contractsToDelete = contractRepository.findExpiredOneOffContractsWithNoSupervisors(deadline);

        for (Contract contract : contractsToDelete) {
            // 조회된 모든 감독자 서명안된 단건 계약을 삭제
            contractRepository.delete(contract);

            // 자동 삭제 알림 전송
            createNotificationUseCase.execute(
                    new NotificationCreateRequest(NotificationType.CONTRACT_AUTO_DELETED, contract.getId(), contract.getUser().getId())
            );

            // 초대 정보 삭제
            deleteInviteInfoUseCase.execute(contract.getId());
        }
    }
} 