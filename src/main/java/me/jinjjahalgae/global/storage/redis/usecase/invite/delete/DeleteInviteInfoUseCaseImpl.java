package me.jinjjahalgae.global.storage.redis.usecase.invite.delete;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteInviteInfoUseCaseImpl implements DeleteInviteInfoUseCase {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.contract-invite}")
    private String CONTRACT_TO_INVITE_PREFIX;

    @Value("${spring.data.redis.contract-supervisors}")
    private String SUPERVISOR_COUNT_PREFIX;

    @Override
    public void execute(Long contractId) {
        String contractKey = CONTRACT_TO_INVITE_PREFIX + contractId;
        Object inviteCode = redisTemplate.opsForValue().get(contractKey);

        // 초대 정보 삭제
        if (inviteCode != null) {
            redisTemplate.delete((String) inviteCode);
        }
        redisTemplate.delete(contractKey);

        // 남은 감독자 자리 정보 삭제
        String supervisorCountKey = SUPERVISOR_COUNT_PREFIX + contractId;
        redisTemplate.delete(supervisorCountKey);
    }

}
