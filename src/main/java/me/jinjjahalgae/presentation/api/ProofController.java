package me.jinjjahalgae.presentation.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jinjjahalgae.domain.proof.dto.request.ProofCreateRequest;
import me.jinjjahalgae.domain.proof.dto.response.*;
import me.jinjjahalgae.domain.proof.usecase.interfaces.*;
import me.jinjjahalgae.global.common.CommonResponse;
import me.jinjjahalgae.presentation.api.docs.proof.ProofControllerDocs;
import org.springframework.http.HttpStatus;
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
    private final GetContractorProofListUseCase getProofListUseCase;
    private final GetRecentProofUseCase getRecentProofUseCase;
    private final GetProofDetailUseCase getProofDetailUseCase;


    @Override
    @PostMapping("/contracts/{contractId}/proofs")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<Void> createProof(@RequestBody ProofCreateRequest req, @PathVariable Long contractId) {
        return null;
    }

    @Override
    @PostMapping("/proofs/{proofId}/again")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<Void> createReProof(@RequestBody ProofCreateRequest req, @PathVariable Long proofId) {
        return null;
    }

    @Override
    @GetMapping("/contracts/{contractId}/proofs/await")
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<List<ProofAwaitResponse>> getAwaitProofs(@PathVariable Long contractId) {
        return null;
    }

    @Override
    @GetMapping("/contracts/{contractId}/proofs/recent")
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<List<ProofRecentResponse>> getRecentProofs(@PathVariable Long contractId) {
        return null;
    }

    @Override
    @GetMapping("/proofs/{proofId}")
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<ProofDetailResponse> getProofDetail(@PathVariable Long proofId) {
        return null;
    }

    @Override
    @GetMapping("/contractors/contracts/{contractId}/proofs")
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<List<ContractorProofListResponse>> getContractorProofList(@PathVariable Long contractId, @RequestParam int year, @RequestParam int month) {
        return null;
    }

    @Override
    @GetMapping("/supervisors/contracts/{contractId}/proofs")
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<List<SupervisorProofListResponse>> getSupervisorProofList(@PathVariable Long contractId, @RequestParam int year, @RequestParam int month) {
        return null;
    }
}
