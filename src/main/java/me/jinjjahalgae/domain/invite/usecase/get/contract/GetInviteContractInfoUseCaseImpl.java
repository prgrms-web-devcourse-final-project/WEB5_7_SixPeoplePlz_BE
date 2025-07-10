package me.jinjjahalgae.domain.invite.usecase.get.contract;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.invite.mapper.InviteMapper;
import me.jinjjahalgae.domain.invite.usecase.get.contract.dto.InviteContractInfoResponse;
import me.jinjjahalgae.domain.participation.entity.Participation;
import me.jinjjahalgae.domain.participation.enums.Role;
import me.jinjjahalgae.domain.user.User;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetInviteContractInfoUseCaseImpl implements GetInviteContractInfoUseCase {

    private final ContractRepository contractRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.contract-supervisors}")
    private String SUPERVISOR_COUNT_PREFIX;

    @Override
    public InviteContractInfoResponse execute(String contractUuid, User user) {
        Contract contract = contractRepository.findByUuid(contractUuid)
                .orElseThrow(() -> ErrorCode.CONTRACT_NOT_FOUND.serviceException("존재하지 않는 계약입니다."));

        // 감독자 자리가 다 찼는지 확인
        String supervisorCountKey = SUPERVISOR_COUNT_PREFIX + contract.getId();
        Integer remaining = (Integer) redisTemplate.opsForValue().get(supervisorCountKey);
        if (remaining == null || remaining <= 0) {
            throw ErrorCode.SUPERVISOR_ALREADY_FULL.serviceException("이미 5명의 감독자가 참여했습니다.");
        }

        // 이미 참여한 사용자인지 확인
        boolean participated = contract.getParticipations()
                .stream()
                .anyMatch(p -> p.getUser().equals(user));
        if (participated) {
            throw ErrorCode.INVITE_ALREADY_PARTICIPATED.serviceException("이미 참여한 계약입니다.");
        }

        // 계약자 정보 조회
        Participation contractorParticipation = contract.getParticipations()
                .stream()
                .filter(p -> p.getRole() == Role.CONTRACTOR)
                .findFirst()
                .orElseThrow(() -> ErrorCode.CONTRACTOR_PARTICIPATION_NOT_FOUND.serviceException("계약자의 정보가 없는 계약입니다."));

        // 계약서 정보 반환
        return InviteMapper.toInviteContractInfoResponse(contract);
    }
}
