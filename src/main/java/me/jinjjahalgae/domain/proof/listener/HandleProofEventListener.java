package me.jinjjahalgae.domain.proof.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jinjjahalgae.domain.proof.event.HandleProofEvent;
import me.jinjjahalgae.domain.proof.usecase.process.ProcessProofStatusUseCase;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class HandleProofEventListener {
    private final ProcessProofStatusUseCase processProofStatusUseCase;

    /**
     * HandleProofEvent를 핸들링하는 리스너
     * 인증id를 통해 인증을 조회하고 피드백 비율에 따라 인증 상태를 변경합니다.
     *
     * @param event 인증 처리 이벤트
     */
    @Async
    @TransactionalEventListener
    public void handle(HandleProofEvent event) {
        processProofStatusUseCase.execute(event.proofId());
    }
}
