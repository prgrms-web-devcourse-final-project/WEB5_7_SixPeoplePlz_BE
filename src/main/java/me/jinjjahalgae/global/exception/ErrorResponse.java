package me.jinjjahalgae.global.exception;

import lombok.Getter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponse {
    private final String code; // 에러 코드
    private final String message; // 에러 메시지

    public static ErrorResponse from(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.name(), errorCode.getMessage());
    }

    public static ErrorResponse of(String message) {
        return new ErrorResponse(null, message);
    }
}