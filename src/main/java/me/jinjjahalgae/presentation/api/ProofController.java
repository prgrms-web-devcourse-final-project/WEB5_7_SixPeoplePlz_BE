package me.jinjjahalgae.presentation.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jinjjahalgae.domain.proof.usecase.create.common.ProofCreateRequest;
import me.jinjjahalgae.domain.proof.usecase.create.proof.CreateProofUseCase;
import me.jinjjahalgae.domain.proof.usecase.create.reproof.CreateReProofUseCase;
import me.jinjjahalgae.domain.proof.usecase.get.await.GetAwaitProofUseCase;
import me.jinjjahalgae.domain.proof.usecase.get.await.ProofAwaitResponse;
import me.jinjjahalgae.domain.proof.usecase.getlist.contractorlist.ContractorProofListResponse;
import me.jinjjahalgae.domain.proof.usecase.getlist.contractorlist.GetContractorProofListUseCase;
import me.jinjjahalgae.domain.proof.usecase.get.detail.GetProofDetailUseCase;
import me.jinjjahalgae.domain.proof.usecase.get.detail.ProofDetailResponse;
import me.jinjjahalgae.domain.proof.usecase.get.recent.GetRecentProofUseCase;
import me.jinjjahalgae.domain.proof.usecase.get.recent.ProofRecentResponse;
import me.jinjjahalgae.domain.proof.usecase.getlist.supervisorlist.GetSupervisorProofListUseCase;
import me.jinjjahalgae.domain.proof.usecase.getlist.supervisorlist.SupervisorProofListResponse;
import me.jinjjahalgae.global.common.CommonResponse;
import me.jinjjahalgae.global.security.jwt.CustomJwtPrincipal;
import me.jinjjahalgae.presentation.api.docs.proof.ProofControllerDocs;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProofController implements ProofControllerDocs {

    private final CreateProofUseCase createProofUseCase;
    private final CreateReProofUseCase createReProofUseCase;
    private final GetAwaitProofUseCase getAwaitProofUseCase;
    private final GetContractorProofListUseCase getContractorProofListUseCase;
    private final GetSupervisorProofListUseCase getSupervisorProofListUseCase;
    private final GetRecentProofUseCase getRecentProofUseCase;
    private final GetProofDetailUseCase getProofDetailUseCase;


    @Override
    @PostMapping("/contracts/{contractId}/proofs")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<Void> createProof(@RequestBody ProofCreateRequest req, @PathVariable Long contractId, @AuthenticationPrincipal CustomJwtPrincipal user) {
        Long userId = user.getUserId();
        createProofUseCase.execute(req, contractId, userId);
        return CommonResponse.success();
    }

    @Override
    @PostMapping("/proofs/{proofId}/again")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<Void> createReProof(@RequestBody ProofCreateRequest req, @PathVariable Long proofId, @AuthenticationPrincipal CustomJwtPrincipal user) {
        Long userId = user.getUserId();
        createReProofUseCase.execute(req, proofId, userId);
        return CommonResponse.success();
    }

    @Override
    @GetMapping("/contracts/{contractId}/proofs/await")
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<List<ProofAwaitResponse>> getAwaitProofs(@PathVariable Long contractId, @AuthenticationPrincipal CustomJwtPrincipal user) {
        Long userId = user.getUserId();
        List<ProofAwaitResponse> result = getAwaitProofUseCase.execute(contractId, userId);
        return CommonResponse.success(result);
    }

    @Override
    @GetMapping("/contracts/{contractId}/proofs/recent")
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<List<ProofRecentResponse>> getRecentProofs(@PathVariable Long contractId, @AuthenticationPrincipal CustomJwtPrincipal user) {
        Long userId = user.getUserId();
        List<ProofRecentResponse> result = getRecentProofUseCase.execute(contractId, userId);
        return CommonResponse.success(result);
    }

    @Override
    @GetMapping("/proofs/{proofId}")
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<ProofDetailResponse> getProofDetail(@PathVariable Long proofId, @AuthenticationPrincipal CustomJwtPrincipal user) {
        Long userId = user.getUserId();
        ProofDetailResponse result = getProofDetailUseCase.execute(proofId, userId);
        return CommonResponse.success(result);
    }

    @Override
    @GetMapping("/contractors/contracts/{contractId}/proofs")
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<List<ContractorProofListResponse>> getContractorProofList(@PathVariable Long contractId,
                                                                                    @RequestParam int year,
                                                                                    @RequestParam int month,
                                                                                    @AuthenticationPrincipal CustomJwtPrincipal user) {
        Long userId = user.getUserId();
        List<ContractorProofListResponse> result = getContractorProofListUseCase.execute(contractId, year, month, userId);
        return CommonResponse.success(result);
    }

    @Override
    @GetMapping("/supervisors/contracts/{contractId}/proofs")
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<List<SupervisorProofListResponse>> getSupervisorProofList(@PathVariable Long contractId,
                                                                                    @RequestParam int year,
                                                                                    @RequestParam int month,
                                                                                    @AuthenticationPrincipal CustomJwtPrincipal user) {
        Long userId = user.getUserId();
        List<SupervisorProofListResponse> result = getSupervisorProofListUseCase.execute(contractId, year, month, userId);
        return CommonResponse.success(result);
    }
}
