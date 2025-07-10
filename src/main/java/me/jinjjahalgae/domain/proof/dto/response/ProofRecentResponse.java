package me.jinjjahalgae.domain.proof.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import me.jinjjahalgae.domain.proof.enums.ProofStatus;

import java.time.LocalDateTime;

/**
 * 최근 3개의 인증 response
 */
@Schema(
        title = "최근 인증 조회",
        description = "최근 3개의 인증 응답 DTO (리스트에 담겨서 보내짐)",
        example = """
        {
          "imageKey": "1234abcd-5678-efgh-ijkl-9012mnopqrst.jpg",
          "comment": null,
          "status": "APPROVED",
          "createdAt": "2025-07-09T13:30:00+09:00",
          "reProof": false,
          "proofId": 20
        }
        """
)
public record ProofRecentResponse(
        @Schema(description = "1 번째 이미지 (대표 사진) 키")
        String imageKey,

        @Schema(description = "인증 코멘트 (없으면 null)")
        String comment,

        @Schema(description = "인증 상태 (APPROVE_PENDING: 승인대기중, APPROVED: 승인완료, REJECTED: 거절됨")
        ProofStatus status,

        @Schema(description = "인증 생성일")
        LocalDateTime createdAt,

        @Schema(description = "재인증 여부 (원본 = false, 재인증 = true")
        boolean reProof,

        @Schema(description = "인증 id")
        long proofId
) {
}
