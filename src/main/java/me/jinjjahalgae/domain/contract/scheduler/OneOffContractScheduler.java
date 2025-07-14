package me.jinjjahalgae.domain.contract.scheduler;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.contract.usecase.process.EndOneOffContractUseCase;
import me.jinjjahalgae.domain.contract.usecase.process.VerifyOneOffContractSignatureUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OneOffContractScheduler {

    private final EndOneOffContractUseCase endOneOffContractUseCase;
    private final VerifyOneOffContractSignatureUseCase verifyOneOffContractSignatureUseCase;

    /**
     * 5분마다 실행되는 스케줄러 (단건 스케쥴러)
     *
     * - 단건 계약 24시간 체크
     * - 단건 계약 서명검증
     */
    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void processOneOffContracts() {
        // 단건 계약 24시간 체크
        endOneOffContractUseCase.execute();
        
        // 단건 계약 서명검증
        verifyOneOffContractSignatureUseCase.execute();
    }
} 