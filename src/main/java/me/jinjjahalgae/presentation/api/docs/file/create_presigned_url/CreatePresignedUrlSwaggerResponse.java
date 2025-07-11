package me.jinjjahalgae.presentation.api.docs.file.create_presigned_url;

import me.jinjjahalgae.domain.file.usecase.presign.dto.CreatePreSignedUrlResponse;
import me.jinjjahalgae.global.common.CommonResponse;

public class CreatePresignedUrlSwaggerResponse extends CommonResponse<CreatePreSignedUrlResponse> {
    public CreatePresignedUrlSwaggerResponse(CreatePreSignedUrlResponse result) {
        super(result);
    }
}