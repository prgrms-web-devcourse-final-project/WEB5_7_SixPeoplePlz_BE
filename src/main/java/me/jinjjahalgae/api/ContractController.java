package me.jinjjahalgae.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.contract.dto.request.ContractCreateRequest;
import me.jinjjahalgae.domain.contract.dto.request.ContractUpdateRequest;
import me.jinjjahalgae.domain.contract.dto.response.ContractCreateResponse;
import me.jinjjahalgae.domain.contract.dto.response.ContractDetailResponse;
import me.jinjjahalgae.domain.contract.dto.response.ContractListResponse;
import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import me.jinjjahalgae.domain.contract.usecase.ContractCreateUseCase;
import me.jinjjahalgae.domain.contract.usecase.ContractDetailUseCase;
import me.jinjjahalgae.domain.contract.usecase.ContractListUseCase;
import me.jinjjahalgae.domain.contract.usecase.ContractUpdateUseCase;
import me.jinjjahalgae.global.common.CommonResponse;
import me.jinjjahalgae.global.security.jwt.CustomJwtPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractCreateUseCase contractCreateUseCase;
    private final ContractListUseCase contractListUseCase;
    private final ContractDetailUseCase contractDetailUseCase;
    private final ContractUpdateUseCase contractUpdateUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<ContractCreateResponse> createContract(
            @AuthenticationPrincipal CustomJwtPrincipal user,
            @Valid @RequestBody ContractCreateRequest request
    ) {
        ContractCreateResponse response = contractCreateUseCase.execute(user.getUserId(), request);
        return CommonResponse.success(response);
    }

    @GetMapping
    public CommonResponse<Page<ContractListResponse>> getContracts(
            @AuthenticationPrincipal CustomJwtPrincipal user,
            @RequestParam List<ContractStatus> statuses,
            @PageableDefault(size = 10) Pageable pageable
    ){
        Page<ContractListResponse> response = contractListUseCase.execute(user.getUserId(), statuses, pageable);
        return CommonResponse.success(response);
    }

    @GetMapping("/{contractId}")
    public CommonResponse<ContractDetailResponse> getContractDetail(
            @AuthenticationPrincipal CustomJwtPrincipal user,
            @PathVariable Long contractId
    ) {
        ContractDetailResponse response = contractDetailUseCase.execute(user.getUserId(), contractId);
        return CommonResponse.success(response);
    }

    @PutMapping("/{contractId}")
    public CommonResponse<Void> updateContract(
            @AuthenticationPrincipal CustomJwtPrincipal user,
            @PathVariable Long contractId,
            @Valid @RequestBody ContractUpdateRequest request
    ) {
        contractUpdateUseCase.execute(user.getUserId(), contractId, request);
        return CommonResponse.success();
    }
}