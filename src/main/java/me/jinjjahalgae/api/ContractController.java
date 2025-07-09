package me.jinjjahalgae.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.contract.dto.request.ContractCreateRequest;
import me.jinjjahalgae.domain.contract.dto.response.ContractCreateResponse;
import me.jinjjahalgae.domain.contract.usecase.ContractCreateUseCase;
import me.jinjjahalgae.global.common.CommonResponse;
import me.jinjjahalgae.global.security.jwt.CustomJwtPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractCreateUseCase contractCreateUseCase;

    @PostMapping
    public CommonResponse<ContractCreateResponse> createContract(
            @AuthenticationPrincipal CustomJwtPrincipal user,
            @Valid @RequestBody ContractCreateRequest request
    ) {
        ContractCreateResponse response = contractCreateUseCase.execute(user.getUserId(), request);
        return CommonResponse.success(response);
    }
}