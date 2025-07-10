package me.jinjjahalgae.domain.contract.scheduler;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ContractStartScheduler {

    private final ContractRepository contractRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${invite.max-supervisors}")
    private int MAX_SUPERVISORS;

    @Value("${spring.data.redis.contract-invite}")
    private String CONTRACT_TO_INVITE_PREFIX;

    @Value("${spring.data.redis.contract-supervisors}")
    private String SUPERVISOR_COUNT_PREFIX;

    // 매일 00시 01분에 실행되어 대기중인 계약을 시작 처리하는 스케줄러
    @Scheduled(cron = "0 1 0 * * *")
    @Transactional
    public void startContracts() {
        LocalDate today = LocalDate.now();

        // 시작일이 오늘인 대기중인 계약 조회
        List<Contract> pendingContracts = contractRepository.findByStatusAndStartDateOn(ContractStatus.PENDING, today);

        // 대기중인 계약이 없는 경우
        if (pendingContracts.isEmpty()) {
            return;
        }

        for (Contract contract : pendingContracts) {
            // redis에서 남은 감독자 자리를 조회해 실제 감독자 수 계산
            int joinedSupervisors = getTotalSupervisor(contract.getId());

            if (joinedSupervisors > 0) {
                // 감독자가 1명 이상이면 db에 총 감독자 수를 업데이트하고 계약 시작
                contract.start(joinedSupervisors);
                // TODO: 계약 시작 알림 필요
            } else {
                // 감독자가 없으면 계약 자동 삭제
                // 계약의 cascade = CascadeType.ALL, orphanRemoval = true 으로 참여 정보 자동 삭제됨
                contractRepository.delete(contract);
                // TODO: 계약 삭제 알림 필요
            }

            // 시작, 삭제 된 계약의 redis 초대 정보는 모두 삭제
            deleteInviteInfo(contract.getId());
        }
    }

    // redis에서 남은 감독자 자리를 조회해 실제 감독자 수 계산
    private int getTotalSupervisor(Long contractId) {
        String supervisorCountKey = SUPERVISOR_COUNT_PREFIX + contractId;

        // redis에서 남은 감독자 자리 수를 가져옴
        Integer remaining = (Integer) redisTemplate.opsForValue().get(supervisorCountKey);

        // redis에 남은 감독자 정보가 없으면 0명으로 지정해 계약이 삭제되게 설정
        if (remaining == null) {
            return 0;
        }

        return MAX_SUPERVISORS - remaining;
    }

    // 시작, 삭제 된 계약의 redis 초대 정보를 삭제
    private void deleteInviteInfo(Long contractId) {
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
