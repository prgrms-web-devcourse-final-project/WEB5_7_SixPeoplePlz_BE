package me.jinjjahalgae.presentation.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.contract.usecase.create.dto.CreateContractRequest;
import me.jinjjahalgae.domain.contract.usecase.delete.CancelContractUseCase;
import me.jinjjahalgae.domain.contract.usecase.delete.WithdrawContractUseCase;
import me.jinjjahalgae.domain.contract.usecase.get.title.GetContractTitleInfoUseCase;
import me.jinjjahalgae.domain.contract.usecase.get.title.dto.ContractTitleInfoResponse;
import me.jinjjahalgae.domain.contract.usecase.update.dto.ContractUpdateRequest;
import me.jinjjahalgae.domain.contract.usecase.create.dto.CreateContractResponse;
import me.jinjjahalgae.domain.contract.usecase.get.detail.dto.ContractDetailResponse;
import me.jinjjahalgae.domain.contract.usecase.get.list.dto.ContractListResponse;
import me.jinjjahalgae.domain.contract.usecase.create.CreateContractUseCase;
import me.jinjjahalgae.domain.contract.usecase.get.detail.GetContractDetailUseCase;
import me.jinjjahalgae.domain.contract.usecase.get.list.GetContractListUseCase;
import me.jinjjahalgae.domain.contract.usecase.update.UpdateContractUseCase;
import me.jinjjahalgae.domain.participation.enums.Role;
import me.jinjjahalgae.global.common.CommonResponse;
import me.jinjjahalgae.global.security.jwt.CustomJwtPrincipal;
import me.jinjjahalgae.presentation.api.docs.contract.ContractControllerDocs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController implements ContractControllerDocs {

    private final CreateContractUseCase createContractUseCase;
    private final GetContractListUseCase getContractListUseCase;
    private final GetContractDetailUseCase getContractDetailUseCase;
    private final UpdateContractUseCase updateContractUseCase;
    private final WithdrawContractUseCase withdrawContractUseCase;
    private final CancelContractUseCase cancelContractUseCase;
    private final GetContractTitleInfoUseCase getContractTitleInfoUseCase;

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<CreateContractResponse> createContract(
            @AuthenticationPrincipal CustomJwtPrincipal user,
            @Valid @RequestBody CreateContractRequest request
    ) {
        CreateContractResponse response = createContractUseCase.execute(user.getUserId(), request);
        return CommonResponse.success(response);
    }

    @Override
    @GetMapping
    public CommonResponse<Page<ContractListResponse>> getContracts(
            @AuthenticationPrincipal CustomJwtPrincipal user,
            @RequestParam Role role,
            @PageableDefault(size = 10) Pageable pageable
    ){
        Page<ContractListResponse> response = getContractListUseCase.execute(user.getUserId(), role, pageable);
        return CommonResponse.success(response);
    }

    @Override
    @GetMapping("/{contractId}")
    public CommonResponse<ContractDetailResponse> getContractDetail(
            @AuthenticationPrincipal CustomJwtPrincipal user,
            @PathVariable Long contractId
    ) {
        ContractDetailResponse response = getContractDetailUseCase.execute(user.getUserId(), contractId);
        return CommonResponse.success(response);
    }

    @Override
    @GetMapping("/{contractId}/titleInfo")
    public CommonResponse<ContractTitleInfoResponse> getContractTitleInfo(
            @AuthenticationPrincipal CustomJwtPrincipal user,
            @PathVariable Long contractId
    ) {
        ContractTitleInfoResponse response = getContractTitleInfoUseCase.execute(user.getUserId(), contractId);
        return CommonResponse.success(response);
    }

    @Override
    @PutMapping("/{contractId}")
    public CommonResponse<Void> updateContract(
            @AuthenticationPrincipal CustomJwtPrincipal user,
            @PathVariable Long contractId,
            @Valid @RequestBody ContractUpdateRequest request
    ) {
        updateContractUseCase.execute(user.getUserId(), contractId, request);
        return CommonResponse.success();
    }

    @Override
    @PatchMapping("/{contractId}/withdraw")
    public CommonResponse<Void> withdrawContract(
            @AuthenticationPrincipal CustomJwtPrincipal user,
            @PathVariable Long contractId
    ) {
        withdrawContractUseCase.execute(user.getUserId(), contractId);
        return CommonResponse.success();
    }

    @Override
    @DeleteMapping("/{contractId}")
    public CommonResponse<Void> cancelContract(
            @AuthenticationPrincipal CustomJwtPrincipal user,
            @PathVariable Long contractId
    ) {
        cancelContractUseCase.execute(user.getUserId(), contractId);
        return CommonResponse.success();
    }
}