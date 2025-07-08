package me.jinjjahalgae.global.exception;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Getter
@JsonPropertyOrder({"success", "code", "message"})
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(
    title = "에러 응답",
    description = "API 요청 실패 시 반환되는 에러 정보"
)
public class ErrorResponse {
    @Schema(
            description = "요청 성공 여부",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private final boolean success = false;

    @Schema(
            description = "에러 코드",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private final String code; // 에러 코드

    @Schema(
            description = "에러 메시지",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private final String message; // 에러 메시지

    public static ErrorResponse from(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.name(), errorCode.getMessage());
    }

    public static ErrorResponse of(String message) {
        return new ErrorResponse(null, message);
    }
}