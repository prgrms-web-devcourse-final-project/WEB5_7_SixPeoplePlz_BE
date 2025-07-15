package me.jinjjahalgae.global.scheduler;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.contract.usecase.process.EndOneOffContractUseCase;
import me.jinjjahalgae.domain.contract.usecase.process.VerifyOneOffContractSignatureUseCase;
import me.jinjjahalgae.domain.proof.usecase.schedule.CheckExpiredProofUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ScheduleManager {

    private final EndOneOffContractUseCase endOneOffContractUseCase;
    private final VerifyOneOffContractSignatureUseCase verifyOneOffContractSignatureUseCase;
    private final CheckExpiredProofUseCase checkExpiredProofUseCase;

    /**
     * 5분마다 실행되는 스케줄러
     *
     * - 단건 계약 24시간 체크
     * - 단건 계약 서명검증
     * - 인증 24시간(만료) 체크
     */
    @Scheduled(cron = "0 */5 * * * *", zone = "Asia/Seoul" )
    @Transactional
    public void fiveMinuteSchedule() {
        // 단건 계약 24시간 체크
        endOneOffContractUseCase.execute();

        // 단건 계약 서명검증
        verifyOneOffContractSignatureUseCase.execute();

        // 인증 24시간(만료) 체크
        checkExpiredProofUseCase.execute(LocalDateTime.now());
    }
}
