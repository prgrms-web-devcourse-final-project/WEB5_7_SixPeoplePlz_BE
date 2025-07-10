package me.jinjjahalgae.presentation.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.file.usecase.create_presigned_url.CreatePresignedUrlUseCase;
import me.jinjjahalgae.domain.file.usecase.create_presigned_url.dto.CreatePreSignedUrlRequest;
import me.jinjjahalgae.domain.file.usecase.create_presigned_url.dto.CreatePreSignedUrlResponse;
import me.jinjjahalgae.global.common.CommonResponse;
import me.jinjjahalgae.presentation.api.docs.file.FileControllerDocs;
import me.jinjjahalgae.global.security.jwt.CustomJwtPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController implements FileControllerDocs {
    private final CreatePresignedUrlUseCase createPresignedUrlUseCase;

    @Override
    @PostMapping("/presigned-url")
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<CreatePreSignedUrlResponse> createPresignedUrl(
            @AuthenticationPrincipal CustomJwtPrincipal user,
            @Valid @RequestBody CreatePreSignedUrlRequest request
    ) {
        CreatePreSignedUrlResponse result = createPresignedUrlUseCase.execute(request);
        
        return CommonResponse.success(result);
    }
} 