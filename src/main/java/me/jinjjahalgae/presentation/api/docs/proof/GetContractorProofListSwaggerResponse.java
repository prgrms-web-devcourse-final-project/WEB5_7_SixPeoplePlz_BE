package me.jinjjahalgae.presentation.api.docs.proof;

import me.jinjjahalgae.domain.proof.usecase.getlist.contractorlist.dto.ContractorProofListResponse;
import me.jinjjahalgae.global.common.CommonResponse;

public class GetContractorProofListSwaggerResponse extends CommonResponse<ContractorProofListResponse> {
    public GetContractorProofListSwaggerResponse(ContractorProofListResponse result) {
        super(result);
    }
}
