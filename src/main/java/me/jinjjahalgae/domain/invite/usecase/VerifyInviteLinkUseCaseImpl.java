package me.jinjjahalgae.domain.invite.usecase;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.invite.usecase.interfaces.VerifyInviteLinkUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerifyInviteLinkUseCaseImpl implements VerifyInviteLinkUseCase {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.contract-invite-prefix}")
    private String CONTRACT_TO_INVITE_PREFIX;

    @Override
    public Boolean execute(String inviteCode) {
        return null;
    }
}
