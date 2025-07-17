package me.jinjjahalgae.global.scheduler.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jinjjahalgae.domain.contract.usecase.process.EndContractsUseCase;
import me.jinjjahalgae.domain.contract.usecase.process.StartContractsUseCase;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContractJobProcessor {

    private final StartContractsUseCase startContractsUseCase;
    private final EndContractsUseCase endContractsUseCase;

    /**
     * @param startContract recover 메서드 구분 용 파라미터
     */
    @Retryable(retryFor = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @Transactional
    public void startContracts(String startContract) {
        startContractsUseCase.execute();
    }

    @Recover
    public void recoverStartContracts(Exception e, String startContract) {
        log.error("startContracts 재시도 실패 - 예외 발생", e);
    }

    /**
     * @param endContract recover 메서드 구분 용 파라미터
     */
    @Retryable(retryFor = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @Transactional
    public void endContracts(String endContract) {
        endContractsUseCase.execute();
    }

    @Recover
    public void recoverEndContracts(Exception e, String endContract) {
        log.error("endContracts 재시도 실패 - 예외 발생", e);
    }
}
