package me.jinjjahalgae.domain.proof.usecase.getlist.contractorlist;

import java.util.List;

public interface GetContractorProofListUseCase {

    List<ContractorProofListResponse> execute(Long contractId, int year, int month, Long userId);
}
