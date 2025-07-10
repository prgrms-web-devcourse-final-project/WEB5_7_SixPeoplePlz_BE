package me.jinjjahalgae.presentation.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jinjjahalgae.domain.participation.dto.request.ParticipationCreateRequest;
import me.jinjjahalgae.domain.participation.usecase.interfaces.CreateSupervisorParticipationUseCase;
import me.jinjjahalgae.domain.participation.usecase.interfaces.DeleteSupervisorParticipationUseCase;
import me.jinjjahalgae.domain.participation.usecase.interfaces.PatchSupervisorParticipationUseCase;
import me.jinjjahalgae.global.common.CommonResponse;
import me.jinjjahalgae.global.security.jwt.CustomJwtPrincipal;
import me.jinjjahalgae.presentation.api.docs.participation.ParticipationControllerDocs;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ParticipationController implements ParticipationControllerDocs {

    private final CreateSupervisorParticipationUseCase createSupervisorParticipationUseCase;
    private final DeleteSupervisorParticipationUseCase deleteSupervisorParticipationUseCase;
    private final PatchSupervisorParticipationUseCase patchSupervisorParticipationUseCase;

    @Override
    @PostMapping("/{contractId}/signature")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<Void> joinAsSupervisor(
            @PathVariable Long contractId,
            @Valid @RequestBody ParticipationCreateRequest request,
            @AuthenticationPrincipal CustomJwtPrincipal principal) {
        createSupervisorParticipationUseCase.execute(contractId, request, principal.getUser());
        return CommonResponse.success();
    }

    @Override
    @DeleteMapping("/{contractId}/supervisors/withdraw")
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<Void> withdrawAsSupervisor(
            @PathVariable Long contractId,
            @AuthenticationPrincipal CustomJwtPrincipal principal) {
        deleteSupervisorParticipationUseCase.execute(contractId, principal.getUser());
        return CommonResponse.success();
    }

    @Override
    @PatchMapping("/{contractId}/supervisors/withdraw")
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<Void> abandonAsSupervisor(
            @PathVariable Long contractId,
            @AuthenticationPrincipal CustomJwtPrincipal principal) {
        patchSupervisorParticipationUseCase.execute(contractId, principal.getUser());
        return CommonResponse.success();
    }
}