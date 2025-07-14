package me.jinjjahalgae.domain.proof.event;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ExpiredProofCheckEvent {
    private final LocalDateTime now;
}
