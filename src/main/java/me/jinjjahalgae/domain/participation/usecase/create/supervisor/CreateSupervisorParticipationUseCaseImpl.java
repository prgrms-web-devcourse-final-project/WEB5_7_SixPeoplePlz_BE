package me.jinjjahalgae.domain.participation.usecase.create.supervisor;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.participation.usecase.common.dto.ParticipationCreateRequest;
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
public class CreateSupervisorParticipationUseCaseImpl implements CreateSupervisorParticipationUseCase {

    private final ContractRepository contractRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.contract-supervisors}")
    private String SUPERVISOR_COUNT_PREFIX;

    @Override
    @Transactional
    public void execute(Long contractId, ParticipationCreateRequest request, User user) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> ErrorCode.CONTRACT_NOT_FOUND.serviceException("존재하지 않는 계약 입니다. id =" + contractId));

        // 이미 참여한 사용자인지 확인
        boolean participated = contract.getParticipations()
                .stream()
                .anyMatch(p -> p.getUser().equals(user));
        if (participated) {
            throw ErrorCode.INVITE_ALREADY_PARTICIPATED.serviceException("이미 참여한 계약입니다.");
        }

        // 감독자 자리가 다 찼는지 확인
        String supervisorCountKey = SUPERVISOR_COUNT_PREFIX + contract.getId();
        Integer remaining = (Integer) redisTemplate.opsForValue().get(supervisorCountKey);
        if (remaining == null || remaining <= 0) {
            throw ErrorCode.SUPERVISOR_ALREADY_FULL.serviceException("이미 5명의 감독자가 참여했습니다.");
        }

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

        // redis의 해당 계약 감독자 자리 감소
        redisTemplate.opsForValue().decrement(supervisorCountKey); // decr 연산으로 원자성 보장
    }
}
