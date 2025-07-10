package me.jinjjahalgae.presentation.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.contract.usecase.create.dto.CreateContractRequest;
import me.jinjjahalgae.domain.contract.usecase.update.ContractUpdateRequest;
import me.jinjjahalgae.domain.contract.usecase.create.dto.CreateContractResponse;
import me.jinjjahalgae.domain.contract.usecase.get.detail.ContractDetailResponse;
import me.jinjjahalgae.domain.contract.usecase.get.list.ContractListResponse;
import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import me.jinjjahalgae.domain.contract.usecase.create.CreateContractUseCase;
import me.jinjjahalgae.domain.contract.usecase.get.detail.GetContractDetailUseCase;
import me.jinjjahalgae.domain.contract.usecase.get.list.GetContractListUseCase;
import me.jinjjahalgae.domain.contract.usecase.update.UpdateContractUpdateUseCase;
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
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final CreateContractUseCase createContractUseCase;
    private final GetContractListUseCase getContractListUseCase;
    private final GetContractDetailUseCase getContractDetailUseCase;
    private final UpdateContractUpdateUseCase updateContractUpdateUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<CreateContractResponse> createContract(
            @AuthenticationPrincipal CustomJwtPrincipal user,
            @Valid @RequestBody CreateContractRequest request
    ) {
        CreateContractResponse response = createContractUseCase.execute(user.getUserId(), request);
        return CommonResponse.success(response);
    }

    @GetMapping
    public CommonResponse<Page<ContractListResponse>> getContracts(
            @AuthenticationPrincipal CustomJwtPrincipal user,
            @RequestParam List<ContractStatus> statuses,
            @PageableDefault(size = 10) Pageable pageable
    ){
        Page<ContractListResponse> response = getContractListUseCase.execute(user.getUserId(), statuses, pageable);
        return CommonResponse.success(response);
    }

    @GetMapping("/{contractId}")
    public CommonResponse<ContractDetailResponse> getContractDetail(
            @AuthenticationPrincipal CustomJwtPrincipal user,
            @PathVariable Long contractId
    ) {
        ContractDetailResponse response = getContractDetailUseCase.execute(user.getUserId(), contractId);
        return CommonResponse.success(response);
    }

    @PutMapping("/{contractId}")
    public CommonResponse<Void> updateContract(
            @AuthenticationPrincipal CustomJwtPrincipal user,
            @PathVariable Long contractId,
            @Valid @RequestBody ContractUpdateRequest request
    ) {
        updateContractUpdateUseCase.execute(user.getUserId(), contractId, request);
        return CommonResponse.success();
    }
}