package me.jinjjahalgae.domain.proof.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "인증 상태",
        example = "APPROVED",
        enumAsRef = true
)
public enum ProofStatus {
    APPROVE_PENDING,
    APPROVED,
    REJECTED,
}
