package me.jinjjahalgae.global.common;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.jinjjahalgae.global.exception.ErrorResponse;

@Getter
@AllArgsConstructor
@JsonSerialize(using = CommonResponseSerializer.class)
public class CommonResponse<T> {

    private final boolean success;
    private final T result;
    private final ErrorResponse error;

    // 성공 응답
    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(true, data, null);
    }

    // 성공 응답 (데이터 미포함)
    public static <T> CommonResponse<T> success() {
        return new CommonResponse<>(true, null, null);
    }

    // 실패 응답
    public static <T> CommonResponse<T> error(ErrorResponse errorResponse) {
        return new CommonResponse<>(false, null, errorResponse);
    }
}