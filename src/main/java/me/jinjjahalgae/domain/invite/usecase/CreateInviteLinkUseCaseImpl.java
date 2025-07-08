package me.jinjjahalgae.domain.invite.usecase;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.invite.dto.InviteInfo;
import me.jinjjahalgae.domain.invite.dto.request.CreateInviteLinkRequest;
import me.jinjjahalgae.domain.invite.dto.response.CreateInviteLinkResponse;
import me.jinjjahalgae.domain.invite.usecase.interfaces.CreateInviteLinkUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CreateInviteLinkUseCaseImpl implements CreateInviteLinkUseCase {

//    private final ContractRepository contractRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${invite.invite_url_prefix}")
    private String INVITE_URL_PREFIX;

    @Value("${invite.expired_time}")
    private long INVITE_EXPIRATION_MINUTES;

    @Value("${spring.data.redis.invite-contract}")
    private String INVITE_CONTRACT_PREFIX;

    @Override
    @Transactional(readOnly = true)
    public CreateInviteLinkResponse execute(CreateInviteLinkRequest request) {
//        Contract contract = contractRepository.findById(request.contractId())
//                .orElseThrow(() -> new Exception("해당하는 계약을 찾을 수 없습니다."));

        // 기존 초대링크가 있으면 반환
        String contractKey = INVITE_CONTRACT_PREFIX + contract.getId();
        Object existingInviteCode = redisTemplate.opsForValue().get(contractKey);
        if (existingInviteCode != null) {
            String inviteCode = (String) existingInviteCode;
            InviteInfo existingInfo = (InviteInfo) redisTemplate.opsForValue().get(inviteCode);

            String inviteUrl = INVITE_URL_PREFIX + inviteCode;
            return new CreateInviteLinkResponse(existingInfo.password(), inviteUrl);
        }

        // 새로운 초대코드 생성
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String newInviteCode = uuid.substring(0, 8);
        String password = uuid.substring(8, 16);

        // 새로운 초대 정보 저장
        InviteInfo inviteInfo = new InviteInfo(contract.getUuid(), password);
        redisTemplate.opsForValue().set(
                newInviteCode,
                inviteInfo,
                INVITE_EXPIRATION_MINUTES,
                TimeUnit.MINUTES
        );
        redisTemplate.opsForValue().set(
                contractKey,
                newInviteCode,
                INVITE_EXPIRATION_MINUTES,
                TimeUnit.MINUTES
        );

        String inviteUrl = INVITE_URL_PREFIX + newInviteCode;
        return new CreateInviteLinkResponse(password, inviteUrl);
    }
}
