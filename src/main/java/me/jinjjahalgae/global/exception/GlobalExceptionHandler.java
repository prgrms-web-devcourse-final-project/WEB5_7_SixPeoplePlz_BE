package me.jinjjahalgae.global.exception;

import lombok.extern.slf4j.Slf4j;
import me.jinjjahalgae.global.common.CommonResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(AppException.class)
    public ResponseEntity<CommonResponse<ErrorResponse>> handleBusinessException(AppException ex) {
        ErrorCode errorCode = ex.getErrorCode();

        log.error("[{}] {} - {}",
                ex.getErrorType(),
                errorCode.name(),
                ex.getDeveloperMessage(),
                ex
        );

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(CommonResponse.error(ErrorResponse.from(errorCode)));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<ErrorResponse>> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                .getAllErrors()
                .get(0)
                .getDefaultMessage();

        log.error("validation 예외: {}", errorMessage, ex);

        return ResponseEntity
                .status(400)
                .body(CommonResponse.error(ErrorResponse.of(errorMessage)));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<ErrorResponse>> handleUnexpectedException(Exception ex) {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

        log.error("예상하지 못한 에러 발생: {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(CommonResponse.error(ErrorResponse.from(errorCode)));
    }
}
