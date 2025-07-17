package me.jinjjahalgae.global.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jinjjahalgae.domain.contract.usecase.process.*;
import me.jinjjahalgae.domain.proof.usecase.schedule.CheckExpiredProofUseCase;
import me.jinjjahalgae.global.scheduler.processor.ContractJobProcessor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduleManager {

    private final EndOneOffContractUseCase endOneOffContractUseCase;
    private final VerifyOneOffContractSignatureUseCase verifyOneOffContractSignatureUseCase;
    private final CheckExpiredProofUseCase checkExpiredProofUseCase;

    private final ContractJobProcessor contractJobProcessor;
    /**
     * 5분마다 실행되는 스케줄러
     *
     * - 단건 계약 24시간 체크
     * - 단건 계약 서명검증
     * - 인증 24시간(만료) 체크
     */
    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void fiveMinuteSchedule() {
        log.info("[Scheduler] 5분 스케줄러 시작. 현재 UTC 시간: {}", Instant.now());

        // 단건 계약 24시간 체크
        endOneOffContractUseCase.execute();

        // 단건 계약 서명검증
        verifyOneOffContractSignatureUseCase.execute();

        // 인증 24시간(만료) 체크
        checkExpiredProofUseCase.execute(Instant.now());
    }

    /**
     * 매일 00:01 에 실행되는 스케줄러
     *
     * - 시작일 확인 후 계약 시작
     * - 성공 실패를 판단 후 계약 종료
     *
     * 재시도
     * - 개별 유스케이스 마다 재시도 수행
     * - 3번 수행 후 최종 실패 시 로그 출력
     */
    @Scheduled(cron = "0 1 0 * * *" )
    public void daySchedule() {
        // 시작일 확인 후 계약 시작
        contractJobProcessor.startContracts("start");

        // 성공 실패를 판단 후 계약 종료
        contractJobProcessor.endContracts("end");
    }
}
