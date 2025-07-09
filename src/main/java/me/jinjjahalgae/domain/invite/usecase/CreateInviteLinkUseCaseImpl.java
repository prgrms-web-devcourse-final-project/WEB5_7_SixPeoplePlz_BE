package me.jinjjahalgae.domain.invite.usecase;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.invite.model.InviteInfo;
import me.jinjjahalgae.domain.invite.dto.response.InviteLinkResponse;
import me.jinjjahalgae.domain.invite.usecase.interfaces.CreateInviteLinkUseCase;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateInviteLinkUseCaseImpl implements CreateInviteLinkUseCase {

    private final ContractRepository contractRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${invite.invite-url-prefix}")
    private String INVITE_URL_PREFIX;

    @Value("${invite.expired-time}")
    private long INVITE_EXPIRATION_HOURS;

    @Value("${invite.max-supervisors}")
    private int MAX_SUPERVISORS;

    @Value("${spring.data.redis.contract-invite}")
    private String CONTRACT_TO_INVITE_PREFIX;

    @Value("${spring.data.redis.contract-supervisors}")
    private String SUPERVISOR_COUNT_PREFIX;

    @Override
    public InviteLinkResponse execute(Long contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> ErrorCode.CONTRACT_NOT_FOUND.serviceException("존재하지 않는 계약 입니다. id=" + contractId));

        // 기존 초대링크가 있으면 반환
        String contractKey = CONTRACT_TO_INVITE_PREFIX + contract.getId();
        Object existingInviteCode = redisTemplate.opsForValue().get(contractKey);
        // 기존 초대 정보를 가져와 응답 작성
        if (existingInviteCode != null) {
            String inviteCode = (String) existingInviteCode;
            Object data = redisTemplate.opsForValue().get(inviteCode);

            if (data != null) {
                InviteInfo existingInfo = objectMapper.convertValue(data, InviteInfo.class);
                String inviteUrl = INVITE_URL_PREFIX + inviteCode;
                return new InviteLinkResponse(inviteUrl, existingInfo.password());
            }
        }

        // 감독자 자리는 redis로 다루어 동시성 해결
        // 만료 시간이 없어 계약 시작 시 이 정보를 받아와 계약의 감독자 수 업데이트 후 redis에서 정리 필요
        String supervisorCountKey = SUPERVISOR_COUNT_PREFIX + contract.getId();
        redisTemplate.opsForValue().setIfAbsent(
                supervisorCountKey,
                MAX_SUPERVISORS
        );

        // 새로운 초대코드 생성
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String newInviteCode = uuid.substring(0, 8);
        String password = uuid.substring(8, 16);

        // 새로운 초대 정보 저장
        InviteInfo inviteInfo = new InviteInfo(contract.getUuid(), password);
        redisTemplate.opsForValue().set(
                newInviteCode,
                inviteInfo,
                INVITE_EXPIRATION_HOURS,
                TimeUnit.HOURS
        );
        redisTemplate.opsForValue().set(
                contractKey,
                newInviteCode,
                INVITE_EXPIRATION_HOURS,
                TimeUnit.HOURS
        );

        // 초대 정보 반환
        String inviteUrl = INVITE_URL_PREFIX + newInviteCode;
        return new InviteLinkResponse(inviteUrl, password);
    }
}