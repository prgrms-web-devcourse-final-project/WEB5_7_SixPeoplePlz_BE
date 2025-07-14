package me.jinjjahalgae.presentation.api.docs.contract;

import io.swagger.v3.oas.annotations.media.Schema;
import me.jinjjahalgae.domain.contract.usecase.get.detail.dto.ContractDetailResponse;
import me.jinjjahalgae.global.common.CommonResponse;

@Schema(name = "계약 상세 조회 응답")
public class GetContractPreviewSwaggerResponse extends CommonResponse<ContractDetailResponse> {
    public GetContractPreviewSwaggerResponse(ContractDetailResponse result) {
        super(result);
    }
}