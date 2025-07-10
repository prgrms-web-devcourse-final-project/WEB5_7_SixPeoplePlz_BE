package me.jinjjahalgae.domain.participation.usecase.delete.supervisor;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.participation.entity.Participation;
import me.jinjjahalgae.domain.participation.enums.Role;
import me.jinjjahalgae.domain.user.User;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteSupervisorParticipationUseCaseImpl implements DeleteSupervisorParticipationUseCase {

    private final ContractRepository contractRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.contract-supervisors}")
    private String SUPERVISOR_COUNT_PREFIX;

    @Override
    @Transactional
    public void execute(Long contractId, User user) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> ErrorCode.CONTRACT_NOT_FOUND.serviceException("존재하지 않는 계약 입니다. id =" + contractId));

        // 감독자 정보 조회
        // 감독자로 있지 않은 계약의 감독 철회 요청을 한 경우 예외 반환
        Participation supervisorParticipation = contract.getParticipations()
                .stream()
                .filter(p -> p.getUser().equals(user) && p.getRole() == Role.SUPERVISOR)
                .findFirst()
                .orElseThrow(() -> ErrorCode.SUPERVISOR_PARTICIPATION_NOT_FOUND.serviceException("해당 계약에 감독으로 참여하고 있지 않습니다."));

        // 계약 시작 전이 아닌데 감독 철회(계약 시작 전 감독 포기)를 요청 한 경우
        if(contract.getStatus() != ContractStatus.PENDING) {
            throw ErrorCode.CANNOT_WITHDRAW_PARTICIPATION_AFTER_START.serviceException("계약이 시작히기 전에만 감독 계약을 철회할 수 있습니다.");
        }

        // 감독자의 참여 정보 제거
        contract.removeParticipation(supervisorParticipation);

        // redis의 해당 계약 감독자 자리 증가
        String supervisorCountKey = SUPERVISOR_COUNT_PREFIX + contract.getId();
        redisTemplate.opsForValue().increment(supervisorCountKey); // incr 연산으로 원자성 보장
    }
}
