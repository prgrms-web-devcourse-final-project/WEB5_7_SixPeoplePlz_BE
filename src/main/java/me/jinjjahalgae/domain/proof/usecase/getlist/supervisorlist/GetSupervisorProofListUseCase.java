package me.jinjjahalgae.domain.proof.usecase.getlist.supervisorlist;

import java.util.List;

public interface GetSupervisorProofListUseCase {
    List<SupervisorProofListResponse> execute(Long contractId, int year, int month, Long userId);
}
