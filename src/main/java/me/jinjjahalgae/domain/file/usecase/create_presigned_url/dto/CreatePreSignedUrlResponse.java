package me.jinjjahalgae.domain.file.usecase.create_presigned_url.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 파일 업로드 이후 반환되는 Presigned URL 응답 dto
 */
@Schema(
        title = "presigned url 생성 응답",
        description = "presigned url 응답 DTO"
)
public record CreatePreSignedUrlResponse(
    @Schema(description = "Presigned URL", example = "https://presigned.com")
    String preSignedUrl,

    @Schema(description = "파일키", example = "3d9ac273-2891-4084-9b29-5c9db453e3e4.png")
    String fileKey
) {}