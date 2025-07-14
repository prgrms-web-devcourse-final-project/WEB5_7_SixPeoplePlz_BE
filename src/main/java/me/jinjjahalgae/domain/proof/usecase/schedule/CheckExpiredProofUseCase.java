package me.jinjjahalgae.domain.proof.usecase.schedule;

import java.time.LocalDateTime;

public interface CheckExpiredProofUseCase {

    void execute(LocalDateTime now);
}
