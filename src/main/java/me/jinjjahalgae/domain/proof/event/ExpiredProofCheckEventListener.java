package me.jinjjahalgae.domain.proof.event;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.proof.usecase.schedule.CheckExpiredProofUseCase;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ExpiredProofCheckEventListener {

    private final CheckExpiredProofUseCase checkExpiredProofUseCase;

    @Async
    @TransactionalEventListener
    public void handleExpiredProofCheckEvent(ExpiredProofCheckEvent event) {
        checkExpiredProofUseCase.execute(event.getNow());
    }
}
