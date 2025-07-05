package me.jinjjahalgae.global.common;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.jinjjahalgae.global.exception.ErrorResponse;

@Getter
@AllArgsConstructor
@JsonSerialize(using = ApiResponseSerializer.class)
public class ApiResponse<T> {

    private final boolean success;
    private final T result;
    private final ErrorResponse error;

    // 성공 응답
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    // 성공 응답 (데이터 미포함)
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(true, null, null);
    }

    // 실패 응답
    public static <T> ApiResponse<T> error(ErrorResponse errorResponse) {
        return new ApiResponse<>(false, null, errorResponse);
    }
}