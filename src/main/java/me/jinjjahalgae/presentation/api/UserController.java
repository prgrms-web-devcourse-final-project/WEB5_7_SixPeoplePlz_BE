package me.jinjjahalgae.presentation.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.user.dto.MyInfoResponse;
import me.jinjjahalgae.domain.user.dto.UpdateMyInfoRequest;
import me.jinjjahalgae.domain.user.usecase.interfaces.DeleteMyAccountUseCase;
import me.jinjjahalgae.domain.user.usecase.interfaces.GetMyInfoUseCase;
import me.jinjjahalgae.domain.user.usecase.interfaces.UpdateMyInfoUseCase;
import me.jinjjahalgae.global.common.CommonResponse;
import me.jinjjahalgae.global.security.jwt.CustomJwtPrincipal;
import me.jinjjahalgae.presentation.api.docs.user.UserControllerDocs;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController implements UserControllerDocs {
    private final GetMyInfoUseCase getMyInfoUseCase;
    private final UpdateMyInfoUseCase updateMyInfoUseCase;
    private final DeleteMyAccountUseCase deleteMyAccountUseCase;

    @Override
    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<MyInfoResponse> getMyInfo(
            @AuthenticationPrincipal CustomJwtPrincipal user
    ) {
        MyInfoResponse response = getMyInfoUseCase.execute(user.getUserId());

        return CommonResponse.success(response);
    }

    @Override
    @PatchMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<MyInfoResponse> updateMyInfo(
            @AuthenticationPrincipal CustomJwtPrincipal user,
            @Valid @RequestBody UpdateMyInfoRequest request
    ) {
        MyInfoResponse response = updateMyInfoUseCase.execute(user.getUserId(), request);

        return CommonResponse.success(response);
    }

    @Override
    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<Void> deleteMyAccount(
            @AuthenticationPrincipal CustomJwtPrincipal user
    ) {
        deleteMyAccountUseCase.execute(user.getUserId());

        return CommonResponse.success();
    }
}
