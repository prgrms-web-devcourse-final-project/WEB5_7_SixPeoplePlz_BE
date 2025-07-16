package me.jinjjahalgae.domain.proof.usecase.getlist.supervisorlist;

import me.jinjjahalgae.domain.proof.usecase.getlist.supervisorlist.dto.SupervisorProofListResponse;

import java.util.List;

public interface GetSupervisorProofListUseCase {
    List<SupervisorProofListResponse> execute(Long contractId, Integer year, Integer month, Long userId);
}
