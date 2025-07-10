package me.jinjjahalgae.presentation.api.docs.file;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.jinjjahalgae.domain.file.usecase.create_presigned_url.dto.CreatePreSignedUrlRequest;
import me.jinjjahalgae.domain.file.usecase.create_presigned_url.dto.CreatePreSignedUrlResponse;
import me.jinjjahalgae.global.common.CommonResponse;
import me.jinjjahalgae.global.exception.ErrorResponse;
import me.jinjjahalgae.global.security.jwt.CustomJwtPrincipal;
import me.jinjjahalgae.presentation.api.docs.file.create_presigned_url.CreatePresignedUrlSwaggerResponse;

@Tag(name = "파일 API", description = "파일 업로드 관련 API")
public interface FileControllerDocs {

    @Operation(
            summary = "Presigned URL 생성",
            description = "파일 업로드를 위한 presigned URL을 생성합니다. <br> - fileName은 필수입니다.<br> - fileName에는 확장자가 포함되어야 합니다. <br><br>  `presigned URL의 유효시간은 10분입니다.` <br><br> `response되는 fileKey로 S3에 PUT한 뒤, 서명, 인증사진 생성에 똑같이 사용하면 됩니다.` <br><br> `파일명만 받기 때문에 이미지 mimetype 검증은 없습니다. 이미지파일을 잘 보내주세요`"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Presigned URL 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreatePresignedUrlSwaggerResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                                            {
                                              "success": true,
                                              "result": {
                                                "preSignedUrl": "https://bucket.s3.ap-northeast-2.amazonaws.com/68e629a0-04aa-46e9-8a25-51430e67667b.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=...",
                                                "fileKey": "12341234-1234-1234-1234-12341234123.png"
                                              }
                                            }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (fileName 누락 또는 유효하지 않은 파일명, 확장자 없음)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "유효하지 않은 fileName",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "BAD_REQUEST",
                                                      "message": "fileName은 필수입니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "유효하지 않은 토큰",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "인증 실패",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "INVALID_TOKEN",
                                                      "message": "유효하지 않은 토큰입니다."
                                                    }"""
                                    ),
                                    @ExampleObject(
                                            name = "만료된 토큰",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "EXPIRED_TOKEN",
                                                      "message": "토큰이 만료되었습니다."
                                                    }"""
                                    )
                            }
                    )
            ),
    })
    CommonResponse<CreatePreSignedUrlResponse> createPresignedUrl(
            @Parameter(hidden = true) CustomJwtPrincipal user,
            @Parameter(description = "파일 업로드 정보", required = true) CreatePreSignedUrlRequest request
    );
} 