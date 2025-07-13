package me.jinjjahalgae.global.storage.redis.usecase.invite.bulk;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BulkDeleteInviteInfoUseCaseImpl implements BulkDeleteInviteInfoUseCase {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.contract-invite}")
    private String CONTRACT_TO_INVITE_PREFIX;

    @Value("${spring.data.redis.contract-supervisors}")
    private String SUPERVISOR_COUNT_PREFIX;

    /**
     * 여러 계약 ID와 관련된 모든 초대 정보를 redis에서 한 번에 삭제
     *
     * @param contractIds 삭제할 계약 ID 목록
     */
    @Override
    public void execute(List<Long> contractIds) {
        if (contractIds == null || contractIds.isEmpty()) return;

        List<String> keysToDelete = new ArrayList<>();

        for (Long contractId : contractIds) {
            // 인덱스 키와 감독자 수 키를 리스트에 추가
            String contractKey = CONTRACT_TO_INVITE_PREFIX + contractId;
            keysToDelete.add(contractKey);

            String supervisorCountKey = SUPERVISOR_COUNT_PREFIX + contractId;
            keysToDelete.add(supervisorCountKey);

            // 초대 코드를 조회하여 리스트에 추가
            Object inviteCode = redisTemplate.opsForValue().get(contractKey);
            if (inviteCode instanceof String) {
                keysToDelete.add((String) inviteCode);
            }
        }

        // 모든 키를 한 번의 명령으로 삭제
        if (!keysToDelete.isEmpty()) {
            redisTemplate.delete(keysToDelete);
        }
    }
}
