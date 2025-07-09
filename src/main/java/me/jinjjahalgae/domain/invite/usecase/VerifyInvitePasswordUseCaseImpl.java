package me.jinjjahalgae.domain.invite.usecase;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.invite.model.InviteInfo;
import me.jinjjahalgae.domain.invite.dto.request.InviteLinkVerifyRequest;
import me.jinjjahalgae.domain.invite.dto.response.ContractUuidResponse;
import me.jinjjahalgae.domain.invite.usecase.interfaces.VerifyInvitePasswordUseCase;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerifyInvitePasswordUseCaseImpl implements VerifyInvitePasswordUseCase {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public ContractUuidResponse execute(String inviteCode, InviteLinkVerifyRequest request) {
        Object data = redisTemplate.opsForValue().get(inviteCode);

        // instanceof로 null 체크와 타입 체크
        if (!(data instanceof InviteInfo inviteInfo)) {
            throw ErrorCode.INVITE_NOT_FOUND.serviceException("존재하지 않거나 만료된 초대입니다. inviteCode: " + inviteCode);
        }

        if (!inviteInfo.password().equals(request.password())) {
            throw ErrorCode.INVALID_INVITE_PASSWORD.serviceException("초대 비밀번호가 일치하지 않습니다. inviteCode: " + inviteCode);
        }

        // 성공 시 contractUuid 반환
        return new ContractUuidResponse(inviteInfo.contractUuid());
    }
}
