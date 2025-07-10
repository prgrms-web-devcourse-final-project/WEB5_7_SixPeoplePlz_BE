package me.jinjjahalgae.presentation.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jinjjahalgae.domain.invite.usecase.verify.password.dto.VerifyInvitePasswordRequest;
import me.jinjjahalgae.domain.invite.usecase.verify.password.dto.ContractUuidResponse;
import me.jinjjahalgae.domain.invite.usecase.get.contract.dto.InviteContractInfoResponse;
import me.jinjjahalgae.domain.invite.usecase.create.invite.dto.InviteLinkResponse;
import me.jinjjahalgae.domain.invite.usecase.create.invite.CreateInviteLinkUseCase;
import me.jinjjahalgae.domain.invite.usecase.get.contract.GetInviteContractInfoUseCase;
import me.jinjjahalgae.domain.invite.usecase.verify.link.VerifyInviteLinkUseCase;
import me.jinjjahalgae.domain.invite.usecase.verify.password.VerifyInvitePasswordUseCase;
import me.jinjjahalgae.global.common.CommonResponse;
import me.jinjjahalgae.global.security.jwt.CustomJwtPrincipal;
import me.jinjjahalgae.presentation.api.docs.invite.InviteControllerDocs;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/invites")
@RequiredArgsConstructor
public class InviteController implements InviteControllerDocs {

    private final CreateInviteLinkUseCase createInviteLinkUseCase;
    private final VerifyInviteLinkUseCase verifyInviteLinkUseCase;
    private final VerifyInvitePasswordUseCase verifyInvitePasswordUseCase;
    private final GetInviteContractInfoUseCase getInviteContractInfoUseCase;

    @Override
    @PostMapping("/{contractId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<InviteLinkResponse> createInviteLink(
            @PathVariable Long contractId,
            @AuthenticationPrincipal CustomJwtPrincipal principal) {
        InviteLinkResponse result = createInviteLinkUseCase.execute(contractId, principal.getUser());
        return CommonResponse.success(result);
    }

    @Override
    @GetMapping("/{inviteCode}")
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<Void> checkInviteLink(@PathVariable String inviteCode) {
        verifyInviteLinkUseCase.execute(inviteCode);
        return CommonResponse.success();
    }

    @Override
    @PostMapping("/{inviteCode}/verify")
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<ContractUuidResponse> verifyPassword(
            @PathVariable String inviteCode,
            @Valid @RequestBody VerifyInvitePasswordRequest request) {
        ContractUuidResponse result = verifyInvitePasswordUseCase.execute(inviteCode, request);
        return CommonResponse.success(result);
    }

    @Override
    @GetMapping("/{inviteCode}/detail/{contractUuid}")
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<InviteContractInfoResponse> getContractInfo(
            @PathVariable String inviteCode,
            @PathVariable String contractUuid,
            @AuthenticationPrincipal CustomJwtPrincipal principal) {
        InviteContractInfoResponse result = getInviteContractInfoUseCase.execute(contractUuid, principal.getUser());
        return CommonResponse.success(result);
    }
}