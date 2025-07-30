package me.jinjjahalgae.domain.participation.usecase.create.supervisor;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.notification.enums.NotificationType;
import me.jinjjahalgae.domain.notification.usecase.listener.event.NotificationEvent;
import me.jinjjahalgae.domain.participation.repository.ParticipationRepository;
import me.jinjjahalgae.domain.participation.usecase.create.contractor.dto.CreateContractorParticipationRequest;
import me.jinjjahalgae.domain.participation.entity.Participation;
import me.jinjjahalgae.domain.participation.enums.Role;
import me.jinjjahalgae.domain.user.User;
import me.jinjjahalgae.global.exception.ErrorCode;
import me.jinjjahalgae.global.storage.redis.usecase.invite.delete.DeleteInviteInfoUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateSupervisorParticipationUseCaseImpl implements CreateSupervisorParticipationUseCase {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ContractRepository contractRepository;
    private final ParticipationRepository participationRepository;
    private final ApplicationEventPublisher eventPublisher;

    private final DeleteInviteInfoUseCase deleteInviteInfoUseCase;

    @Value("${spring.data.redis.contract-supervisors}")
    private String SUPERVISOR_COUNT_PREFIX;

    @Override
    @Transactional
    public void execute(Long contractId, CreateContractorParticipationRequest request, User user) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> ErrorCode.CONTRACT_NOT_FOUND.serviceException("존재하지 않는 계약 입니다. id =" + contractId));

        // 계약 시작 전이 아닌데 서명 한 경우
        if(contract.getStatus() != ContractStatus.PENDING) {
            throw ErrorCode.CANNOT_PARTICIPATE_AFTER_START.serviceException("시작 전인 계약만 서명할 수 있습니다.");
        }

        // 본인이 계약자인지 확인
        boolean isContractor = participationRepository.existsByContractIdAndUserIdAndRole(contractId, user.getId(), Role.CONTRACTOR);

        if (isContractor) {
            throw ErrorCode.CANNOT_PARTICIPATE_SELF_CONTRACT.serviceException("본인이 생성한 계약에는 감독자로 참여할 수 없습니다.");
        }

        // 이미 참여한 사용자인지 확인
        boolean isParticipated = participationRepository.existsByContractIdAndUserId(contract.getId(), user.getId());

        if (isParticipated) {
            throw ErrorCode.INVITE_ALREADY_PARTICIPATED.serviceException("이미 참여한 계약입니다.");
        }

        // 감독자 자리가 다 찼는지 확인
        String supervisorCountKey = SUPERVISOR_COUNT_PREFIX + contract.getId();

        // 자리 감소
        Long remaining = redisTemplate.opsForValue().decrement(supervisorCountKey);

        // 감소시킨 후의 값을 확인
        if (remaining == null) {
            throw ErrorCode.INVITE_NOT_FOUND.serviceException("존재하지 않거나 만료된 초대정보 입니다.");
        }
        if (remaining < 0) {
            // 자리가 없는데 차감된 경우 다시 증가시켜 놓고 예외를 발생
            redisTemplate.opsForValue().increment(supervisorCountKey);
            throw ErrorCode.SUPERVISOR_ALREADY_FULL.serviceException("이미 5명의 감독자가 참여했습니다.");
        }

        try {
            // 새로운 참여 정보 생성
            Participation newParticipation = Participation.builder()
                    .contract(contract)
                    .user(user)
                    .imageKey(request.imageKey())
                    .role(Role.SUPERVISOR)
                    .valid(true)
                    .build();

            // 계약에 참여 정보 추가 및 감독자 수 증가
            contract.addParticipation(newParticipation);

            // 감독자 참여 알림 전송
            eventPublisher.publishEvent(new NotificationEvent(
                    NotificationType.SUPERVISOR_ADDED,
                    contractId,
                    user.getId()
            ));

            // 만약 단발성 계약이면 바로 시작처리
            if (contract.isOneOff()) {
                // 감독자 수를 1로 설정하고 계약 시작 (시작일, 종료일 이 시점으로 재갱신 - 지금부터 24시간동안 계약진행)
                contract.startOneOffContract(1);

                // 계약 시작 알림 발송 - 모든 참여자에게 전송
                // 계약자에게 알림
                eventPublisher.publishEvent(new NotificationEvent(
                        NotificationType.CONTRACT_STARTED,
                        contract.getId(),
                        contract.getUser().getId()
                ));

                // 초대 정보 삭제
                deleteInviteInfoUseCase.execute(contract.getId());
            }

        } catch (Exception exception) {
            // 트랜잭션 중 예외 발생 시 감소시켰던 Redis 카운터 복구
            redisTemplate.opsForValue().increment(supervisorCountKey);
            throw exception;
        }
    }
}
