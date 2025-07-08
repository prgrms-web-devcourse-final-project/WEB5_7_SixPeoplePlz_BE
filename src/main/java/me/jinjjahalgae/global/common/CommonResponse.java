package me.jinjjahalgae.global.common;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.global.exception.ErrorResponse;

@Getter
@JsonPropertyOrder({"success", "result"})
@RequiredArgsConstructor
@Schema(
    title = "공통 응답",
    description = "**API 응답의 공통 형식.** <br>**성공 시** result 필드에 데이터가 포함되고 <br>**실패 시** error 필드에 에러 정보가 포함됩니다."
)
public class CommonResponse<T> {

    @Schema(
            description = "요청 성공 여부",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "true"
    )
    private final boolean success = true;

    @Schema(
            description = "응답 데이터 ",
            example = "Any Data"
    )
    private final T result;

    // 성공 응답
    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(data);
    }

    // 성공 응답 (데이터 미포함)
    public static <T> CommonResponse<T> success() {
        return new CommonResponse<>(null);
    }
}