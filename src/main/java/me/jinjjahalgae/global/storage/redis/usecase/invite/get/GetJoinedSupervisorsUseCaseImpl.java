package me.jinjjahalgae.global.storage.redis.usecase.invite.get;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetJoinedSupervisorsUseCaseImpl implements GetJoinedSupervisorsUseCase {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${invite.max-supervisors}")
    private int MAX_SUPERVISORS;

    @Value("${spring.data.redis.contract-supervisors}")
    private String SUPERVISOR_COUNT_PREFIX;

    @Override
    public int execute(Long contractId) {
        String supervisorCountKey = SUPERVISOR_COUNT_PREFIX + contractId;

        // redis에서 남은 감독자 자리 수 조회
        Integer remaining = (Integer) redisTemplate.opsForValue().get(supervisorCountKey);

        // redis에 남은 감독자 정보가 없으면 0명으로 지정해 계약이 삭제되게 설정
        if (remaining == null) {
            return 0;
        }

        return MAX_SUPERVISORS - remaining;
    }
}
