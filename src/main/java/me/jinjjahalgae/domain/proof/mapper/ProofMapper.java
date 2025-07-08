package me.jinjjahalgae.domain.proof.mapper;

import me.jinjjahalgae.domain.proof.entities.Proof;
import me.jinjjahalgae.domain.proof.enums.ProofStatus;

public class ProofMapper {

    public static Proof toEntity(String comment, int totalSupervisors, Long contractId) {
        return Proof.builder()
                .comment(comment)
                .totalSupervisors(totalSupervisors)
                .checkedSupervisors(0)
                .status(ProofStatus.APPROVE_PENDING)
                .contractId(contractId)
                .build();
    }
}
