package me.jinjjahalgae.domain.proof.usecase.get.await;

import io.swagger.v3.oas.annotations.media.Schema;
import me.jinjjahalgae.domain.proof.enums.ProofStatus;

import java.time.LocalDateTime;

/**
 * 대기중인 인증 조회를 위한 response
 */
@Schema(
        title = "감독자용 대기중인 인증 조회",
        description = "승인 대기중인 인증 조회 응답 DTO",
        example = """
        {
          "imageKey": "1234abcd-5678-efgh-ijkl-9012mnopqrst.jpg",
          "comment": "6시에 헬스장 가서 7시30분까지 운동했습니다.",
          "createdAt": "2025-07-09T13:30:00+09:00",
          "status": "APPROVE_PENDING",
          "reProof": false,
          "proofId": 11
        }
        """
)
public record ProofAwaitResponse(
        @Schema(description = "1 번째 이미지 (대표 사진) 키")
        String imageKey,

        @Schema(description = "인증 코멘트 (없으면 null)")
        String comment,

        @Schema(description = "인증 생성 시간 -> 인증 카드의 HH남음, mm남음 계산용")
        LocalDateTime createdAt,

        @Schema(description = "대기중 상태 APPROVE_PENDING (다른 상태가 전달되는 경우 예외)")
        ProofStatus status,

        @Schema(description = "원본/재인증 여부 (재인증 = true, 원본 = false) 태그 출력용")
        boolean reProof,

        @Schema(description = "인증 id")
        long proofId
) {}
