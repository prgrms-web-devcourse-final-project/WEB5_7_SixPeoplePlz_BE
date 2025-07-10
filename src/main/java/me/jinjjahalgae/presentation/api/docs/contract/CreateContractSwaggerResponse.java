package me.jinjjahalgae.presentation.api.docs.contract;

import io.swagger.v3.oas.annotations.media.Schema;
import me.jinjjahalgae.domain.contract.usecase.create.dto.CreateContractResponse;
import me.jinjjahalgae.global.common.CommonResponse;

@Schema(name = "계약 생성 응답")
public class CreateContractSwaggerResponse extends CommonResponse<CreateContractResponse> {
    public CreateContractSwaggerResponse(CreateContractResponse result) {
        super(result);
    }
}