package me.jinjjahalgae.domain.proof.usecase.interfaces;

import me.jinjjahalgae.domain.proof.dto.response.ContractorProofListResponse;

import java.util.List;

public interface GetContractorProofListUseCase {

    List<ContractorProofListResponse> execute(Long contractId, int year, int month, Long userId);
}
