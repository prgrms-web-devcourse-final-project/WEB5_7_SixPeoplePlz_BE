package me.jinjjahalgae.domain.proof.usecase.create.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 인증 생성 request
 * 재인증도 동일하게 사용
 */
@Schema(
        title = "인증/재인증 생성 요청",
        description = "인증/재인증 생성 요청 DTO"
)
public record ProofCreateRequest(
        @Schema(
                description = "1 번째 이미지 키",
                requiredMode = Schema.RequiredMode.REQUIRED,
                example = "1234abcd-5678-efgh-ijkl-9012mnopqrst.jpg"
        )
        @NotBlank(message = "firstImageKey는 필수입니다.") String firstImageKey,

        @Schema(
                description = "2 번째 이미지 키",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                example = "7774dfsf-5678-efgh-ijkl-9012mnopqrst.jpg"
        )
        String secondImageKey,

        @Schema(
                description = "3 번째 이미지 키",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                example = "1784qwrr-5678-efgh-ijkl-9012mnopqrst.jpg"
        )
        String thirdImageKey,

        @Schema(
                description = "선택 입력 받는 인증 코멘트",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                example = "6시에 헬스장 가서 7시30분까지 운동했습니다."
        )
        String comment
) {
}
