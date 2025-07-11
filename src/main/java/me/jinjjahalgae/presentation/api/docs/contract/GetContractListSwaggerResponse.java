package me.jinjjahalgae.presentation.api.docs.contract;

import io.swagger.v3.oas.annotations.media.Schema;
import me.jinjjahalgae.domain.contract.usecase.get.list.dto.ContractListResponse;
import me.jinjjahalgae.global.common.CommonResponse;
import org.springframework.data.domain.Page;

@Schema(name = "계약 목록 조회 응답")
public class GetContractListSwaggerResponse extends CommonResponse<Page<ContractListResponse>> {
    public GetContractListSwaggerResponse(Page<ContractListResponse> result) {
        super(result);
    }
}