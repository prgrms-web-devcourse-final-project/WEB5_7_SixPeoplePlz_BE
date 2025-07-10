package me.jinjjahalgae.domain.file.usecase.create_presigned_url;

import me.jinjjahalgae.domain.file.usecase.create_presigned_url.dto.CreatePreSignedUrlRequest;
import me.jinjjahalgae.domain.file.usecase.create_presigned_url.dto.CreatePreSignedUrlResponse;

/**
 * 파일 업로드 시 사용할 Presigned URL 생성 요청
 */
public interface CreatePresignedUrlUseCase {
    CreatePreSignedUrlResponse execute(CreatePreSignedUrlRequest request);
} 