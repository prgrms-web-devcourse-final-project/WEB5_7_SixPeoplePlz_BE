package me.jinjjahalgae.presentation.api.docs.contract;

import io.swagger.v3.oas.annotations.media.Schema;
import me.jinjjahalgae.domain.contract.usecase.get.title.dto.ContractTitleInfoResponse;
import me.jinjjahalgae.global.common.CommonResponse;

@Schema(name = "계약 제목 정보 조회 응답")
public class GetContractTitleInfoSwaggerResponse extends CommonResponse<ContractTitleInfoResponse> {
    public GetContractTitleInfoSwaggerResponse(ContractTitleInfoResponse result) {
        super(result);
    }
}