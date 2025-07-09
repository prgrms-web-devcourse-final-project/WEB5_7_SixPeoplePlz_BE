package me.jinjjahalgae.domain.proof.usecase.getsupervisorprooflistusecase;

import java.util.List;

public interface GetSupervisorProofListUseCase {
    List<SupervisorProofListResponse> execute(Long contractId, int year, int month, Long userId);
}
