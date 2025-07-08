package me.jinjjahalgae.domain.invite.usecase;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.contract.entity.Contract; // import 추가
import me.jinjjahalgae.domain.contract.repository.ContractRepository; // import 추가
import me.jinjjahalgae.domain.invite.dto.InviteInfo;
import me.jinjjahalgae.domain.invite.dto.response.InviteLinkResponse;
import me.jinjjahalgae.domain.invite.usecase.interfaces.CreateInviteLinkUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CreateInviteLinkUseCaseImpl implements CreateInviteLinkUseCase {

    private final ContractRepository contractRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${invite.invite-url-prefix}")
    private String INVITE_URL_PREFIX;

    @Value("${invite.expired-time-hours}")
    private long INVITE_EXPIRATION_HOURS;

    @Value("${invite.max-supervisors}")
    private int MAX_SUPERVISORS;

    // application.yml에 정의된 값을 사용하도록 수정
    @Value("${spring.data.redis.contract-invite-prefix}")
    private String CONTRACT_TO_INVITE_PREFIX;

    @Value("${spring.data.redis.supervisor-count-prefix}")
    private String SUPERVISOR_COUNT_PREFIX;

    @Override
    @Transactional(readOnly = true)
    public InviteLinkResponse execute(Integer contractId) {
        // 반환하는 예외는 임시
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new NoSuchElementException("해당하는 계약을 찾을 수 없습니다."));

        // 기존 초대링크가 있으면 반환
        String contractKey = CONTRACT_TO_INVITE_PREFIX + contract.getId();
        Object existingInviteCode = redisTemplate.opsForValue().get(contractKey);
        if (existingInviteCode != null) {
            String inviteCode = (String) existingInviteCode;
            Object data = redisTemplate.opsForValue().get(inviteCode);

            // instanceof를 이용해 null 체크와 타입 체크를 동시에 수행
            if (data instanceof InviteInfo existingInfo) {
                String inviteUrl = INVITE_URL_PREFIX + inviteCode;
                return new InviteLinkResponse(existingInfo.password(), inviteUrl);
            }
        }

        // 감독자 자리는 redis로 다루어 동시성 해결
        // 계약 시작 시 이 정보를 받아와 계약의 감독자 수 업데이트 후 redis에서 정리 필요
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
        redisTemplate.opsForValue().set(newInviteCode, inviteInfo, INVITE_EXPIRATION_HOURS, TimeUnit.HOURS);
        redisTemplate.opsForValue().set(contractKey, newInviteCode, INVITE_EXPIRATION_HOURS, TimeUnit.HOURS);

        String inviteUrl = INVITE_URL_PREFIX + newInviteCode;
        return new InviteLinkResponse(password, inviteUrl);
    }
}