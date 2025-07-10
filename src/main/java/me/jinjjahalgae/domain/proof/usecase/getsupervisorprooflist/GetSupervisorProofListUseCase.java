package me.jinjjahalgae.domain.proof.usecase.getsupervisorprooflist;

import java.util.List;

public interface GetSupervisorProofListUseCase {
    List<SupervisorProofListResponse> execute(Long contractId, int year, int month, Long userId);
}
