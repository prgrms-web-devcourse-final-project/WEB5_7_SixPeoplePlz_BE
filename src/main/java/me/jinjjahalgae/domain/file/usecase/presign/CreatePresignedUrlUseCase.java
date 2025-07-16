package me.jinjjahalgae.domain.file.usecase.presign;

import me.jinjjahalgae.domain.file.usecase.presign.dto.CreatePreSignedUrlRequest;
import me.jinjjahalgae.domain.file.usecase.presign.dto.CreatePreSignedUrlResponse;

/**
 * 파일 업로드 시 사용할 Presigned URL 생성 요청
 */
public interface CreatePresignedUrlUseCase {
    CreatePreSignedUrlResponse execute(CreatePreSignedUrlRequest request);
} 