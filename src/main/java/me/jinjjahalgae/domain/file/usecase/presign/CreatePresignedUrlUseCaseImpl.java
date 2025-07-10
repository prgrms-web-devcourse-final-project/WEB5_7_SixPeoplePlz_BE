package me.jinjjahalgae.domain.file.usecase.presign;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.file.mapper.FileMapper;
import me.jinjjahalgae.domain.file.usecase.presign.dto.CreatePreSignedUrlRequest;
import me.jinjjahalgae.domain.file.usecase.presign.dto.CreatePreSignedUrlResponse;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

/**
 * 파일 업로드 시 사용할 Presigned URL 생성 요청
 */
@Service
@RequiredArgsConstructor
public class CreatePresignedUrlUseCaseImpl implements CreatePresignedUrlUseCase {
    private final S3Presigner s3Presigner;
    private final FileMapper fileMapper;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    public CreatePreSignedUrlResponse execute(CreatePreSignedUrlRequest request) {
        // 스토리지에 저장될 파일 고유 키 생성
        String objectKey = createObjectKey(request.fileName());

        // 클라이언트가 스토리지에 직접 파일을 업로드할 수 있는 presigned url 생성
        String preSignedUrl = createPreSignedUrl(objectKey);

        // presigned url, objectKey 반환
        return fileMapper.toCreatePreSignedUrlResponse(preSignedUrl, objectKey);
    }


    // S3에 저장될 파일의 고유한 키 생성
    private String createObjectKey(String fileName) {
        String extension = getExtension(fileName);

        return UUID.randomUUID() + "." + extension;
    }

    // 확장자 추출
    private String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');

        // '.' 이 없거나 파일 이름의 맨 앞에 있어서 확장자가 없는 경우
        if (dotIndex == -1 || dotIndex == 0) {
            throw ErrorCode.BAD_REQUEST.serviceException("유효하지않은 파일명 {}:" + fileName);
        }

        return fileName.substring(dotIndex + 1);
    }

    // presigned url 생성
    private String createPreSignedUrl(String objectKey) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucket).key(objectKey).build();

        // presigned url 생성에 필요한 설정 세팅
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder().signatureDuration(Duration.ofMinutes(10)).putObjectRequest(putObjectRequest).build();

        // presigned url 생성
        return s3Presigner.presignPutObject(presignRequest).url().toString();
    }
} 