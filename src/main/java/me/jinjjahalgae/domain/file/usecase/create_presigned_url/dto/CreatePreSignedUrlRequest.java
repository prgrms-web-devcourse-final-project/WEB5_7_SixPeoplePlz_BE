package me.jinjjahalgae.domain.file.usecase.create_presigned_url.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 파일 업로드 시 사용할 Presigned URL 생성 요청 dto
 */
@Schema(
        title = "presigned url 생성 요청",
        description = "presigned url 생성 DTO"
)
public record CreatePreSignedUrlRequest(
    @Schema(description = "파일 이름", example = "image.png")
    @NotBlank(message = "fileName은 필수입니다.")
    String fileName
) {
} 