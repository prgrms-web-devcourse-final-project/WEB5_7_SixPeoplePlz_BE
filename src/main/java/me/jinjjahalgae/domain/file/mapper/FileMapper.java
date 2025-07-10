package me.jinjjahalgae.domain.file.mapper;

import me.jinjjahalgae.domain.file.usecase.create_presigned_url.dto.CreatePreSignedUrlResponse;
import org.springframework.stereotype.Component;

/**
 * File 도메인 매퍼
 */
@Component
public class FileMapper {

    /**
     * presigned URL과 fileKey를 CreatePreSignedUrlResponse로 매핑
     */
    public CreatePreSignedUrlResponse toCreatePreSignedUrlResponse(String preSignedUrl, String fileKey) {
        return new CreatePreSignedUrlResponse(preSignedUrl, fileKey);
    }
} 