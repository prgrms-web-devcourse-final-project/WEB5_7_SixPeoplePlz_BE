package me.jinjjahalgae.domain.invite.usecase.verify.link;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerifyInviteLinkUseCaseImpl implements VerifyInviteLinkUseCase {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void execute(String inviteCode) {
        // inviteCode에 해당하는 초대 정보가 있는지 확인
        boolean isValid = redisTemplate.hasKey(inviteCode);

        // 초대 정보가 없는 경우 예외 반환
        if (!isValid) {
            throw ErrorCode.INVITE_NOT_FOUND.serviceException("존재하지 않거나 만료된 초대입니다. inviteCode: " + inviteCode);
        }
    }
}
