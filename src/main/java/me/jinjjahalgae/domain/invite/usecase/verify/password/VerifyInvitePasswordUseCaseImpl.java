package me.jinjjahalgae.domain.invite.usecase.verify.password;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.invite.mapper.InviteMapper;
import me.jinjjahalgae.domain.invite.model.InviteInfo;
import me.jinjjahalgae.domain.invite.usecase.verify.password.dto.VerifyInvitePasswordRequest;
import me.jinjjahalgae.domain.invite.usecase.verify.password.dto.ContractUuidResponse;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerifyInvitePasswordUseCaseImpl implements VerifyInvitePasswordUseCase {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public ContractUuidResponse execute(String inviteCode, VerifyInvitePasswordRequest request) {
        Object data = redisTemplate.opsForValue().get(inviteCode);

        // 초대 정보가 존재하는지 확인
        if (data == null) {
            throw ErrorCode.INVITE_NOT_FOUND.serviceException("존재하지 않거나 만료된 초대입니다. inviteCode: " + inviteCode);
        }

        // 가져온 초대 정보 변환
        InviteInfo inviteInfo = objectMapper.convertValue(data, InviteInfo.class);

        // 비밀번호가 일치하지 않는 경우 예외 발생
        if (!inviteInfo.password().equals(request.password())) {
            throw ErrorCode.INVALID_INVITE_PASSWORD.serviceException("초대 비밀번호가 일치하지 않습니다. inviteCode: " + inviteCode);
        }

        // 계약의 UUID 반환
        return InviteMapper.toContractUuidResponse(inviteInfo.contractUuid());
    }
}
