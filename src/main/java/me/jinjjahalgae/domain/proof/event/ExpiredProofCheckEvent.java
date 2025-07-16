package me.jinjjahalgae.domain.proof.event;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@Getter
@RequiredArgsConstructor
public class ExpiredProofCheckEvent {
    private final Instant now;
}
