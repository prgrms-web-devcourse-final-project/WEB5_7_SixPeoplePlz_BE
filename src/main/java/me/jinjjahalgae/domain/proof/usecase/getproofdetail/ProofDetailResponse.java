package me.jinjjahalgae.domain.proof.usecase.getproofdetail;

import io.swagger.v3.oas.annotations.media.Schema;
import me.jinjjahalgae.domain.feedback.dto.FeedbackResponse;
import me.jinjjahalgae.domain.proof.enums.ProofStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 인증 상세 response
 */
@Schema(
        title = "인증 상세 페이지 조회",
        description = "인증 상세 페이지 응답 DTO",
        example = """
        {
          "imageKeys": [
            "1234abcd-5678-efgh-ijkl-9012mnopqrst.jpg",
            "5678ssss-5678-efgh-ijkl-9012mnopqrst.jpg",
            "1468qwer-5678-efgh-ijkl-9012mnopqrst.jpg"
          ],
          "comment": "6시에 헬스장 가서 7시30분까지 운동했습니다.",
          "status": "REJECTED",
          "createdAt": "2025-07-09T13:30:00",
          "reProof": false,
          "feedbacks": [
            {
              "createdAt": "2025-07-09T13:30:00",
              "status": "APPROVED",
              "comment": "확인했습니다!"
            },
            {
              "createdAt": "2025-07-09T14:55:00",
              "status": "REJECTED",
              "comment": "사진만 찍고 온거 아님?"
            },
            {
              "createdAt": "2025-07-09T16:00:00",
              "status": "REJECTED",
              "comment": null
            }
          ],
          "proofId": 17
        }
        """
)
public record ProofDetailResponse(
   @Schema(description = "인증 이미지 키들 (1 번 이미지부터 순서대로 정렬된 상태)")
   List<String> imageKeys,

   @Schema(description = "계약자가 작성한 인증 코멘트 (없으면 null)")
   String comment,

   @Schema(description = "인증 상태 (APPROVED: 승인완료, REJECTED: 거절됨)")
   ProofStatus status,

   @Schema(description = "인증 생성 일자 (몇 일자 인증인지)")
   LocalDateTime createdAt,

   @Schema(description = "재인증 여부 (원본 = false, 재인증 = true)")
   boolean reProof,

   @Schema(description = "인증에 달린 감독자들의 피드백 데이터들")
   List<FeedbackResponse> feedbacks,

   @Schema(description = "인증 id")
   long proofId
) {}
