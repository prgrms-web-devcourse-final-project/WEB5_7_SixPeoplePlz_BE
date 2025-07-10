package me.jinjjahalgae.presentation.api.docs.notification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.jinjjahalgae.global.common.CommonResponse;
import me.jinjjahalgae.global.exception.ErrorResponse;
import me.jinjjahalgae.presentation.api.docs.NoContentSwaggerResponse;
import me.jinjjahalgae.domain.notification.usecase.getAllNotification.NotificationGetResponse;
import me.jinjjahalgae.global.security.jwt.CustomJwtPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Tag(name = "알림 API", description = "알림 목록 조회, 읽지 않은 개수, 삭제, 읽음 처리 API")
public interface NotificationControllerDocs {

    @Operation(
        summary = "알림 목록 조회",
        description = "현재 로그인한 사용자의 모든 알림 목록을 페이지네이션으로 조회합니다.",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = NotificationGetResponse.class),
                examples = @ExampleObject(
                    name = "성공 응답",
                    value = """
                    {
                      "success": true,
                      "result": {
                        "content": [
                          {
                            "id": 1,
                            "type": "SUPERVISOR_ADDED",
                            "content": "'ㅇㅇㅇ'님이 'ㅇㅇㅇ' 계약의 감독자로 추가되었습니다.",
                            "read": false,
                            "createdAt": "2024-05-01T12:00:00"
                          }
                        ],
                        "pageable": {...},
                        "totalPages": 1,
                        "totalElements": 1
                      }
                    }
                    """
                )
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
        )
    })
    CommonResponse<Page<NotificationGetResponse>> getAllNotifications(
        @Parameter(hidden = true) CustomJwtPrincipal principal,
        @Parameter(description = "페이지네이션 정보", hidden = true) Pageable pageable
    );

    @Operation(
        summary = "읽지 않은 알림 개수 조회",
        description = "현재 로그인한 사용자가 읽지 않은 알림의 개수를 조회합니다.",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Long.class),
                examples = @ExampleObject(
                    name = "성공 응답",
                    value = """
                    {
                      "success": true,
                      "result": 3
                    }
                    """
                )
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
        )
    })
    CommonResponse<Long> countUnreadNotificationByUserId(
        @Parameter(hidden = true) CustomJwtPrincipal principal
    );

    @Operation(
        summary = "알림 전체 삭제",
        description = "현재 로그인한 사용자의 모든 알림을 삭제합니다.",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "삭제 성공 (삭제된 알림 개수 반환)",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Long.class),
                examples = @ExampleObject(
                    name = "성공 응답",
                    value = """
                    {
                      "success": true,
                      "result": 5
                    }
                    """
                )
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
        )
    })
    CommonResponse<Long> deleteAllNotification(
        @Parameter(hidden = true) CustomJwtPrincipal principal
    );

    @Operation(
        summary = "알림 단건 삭제",
        description = "주어진 알림 ID에 해당하는 알림을 삭제합니다.",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "삭제 성공 (응답 없음)",
            content = @Content(schema = @Schema(implementation = NoContentSwaggerResponse.class))
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
        @ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 알림",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    CommonResponse<Void> deleteSingleNotification(
        @Parameter(description = "알림 ID", example = "1") Long notificationId
    );

    @Operation(
        summary = "알림 읽음 처리",
        description = "주어진 알림 ID에 해당하는 알림을 읽음 상태로 설정합니다.",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "읽음 처리 성공 (응답 없음)",
            content = @Content(schema = @Schema(implementation = NoContentSwaggerResponse.class))
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
        @ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 알림",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    CommonResponse<Void> markSingleNotificationAsRead(
        @Parameter(description = "알림 ID", example = "1") Long notificationId
    );
}
