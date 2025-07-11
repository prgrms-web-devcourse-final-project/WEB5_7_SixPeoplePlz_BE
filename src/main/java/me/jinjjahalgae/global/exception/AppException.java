package me.jinjjahalgae.global.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String developerMessage;
    private final ErrorType errorType;

    public AppException(ErrorCode errorCode, String developerMessage, ErrorType errorType) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.developerMessage = developerMessage;
        this.errorType = errorType;
    }

    public AppException(ErrorCode errorCode, ErrorType errorType) {
        this.errorCode = errorCode;
        this.developerMessage = null;
        this.errorType = errorType;
    }
}