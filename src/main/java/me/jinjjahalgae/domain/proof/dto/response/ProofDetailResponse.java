package me.jinjjahalgae.domain.proof.dto.response;

import me.jinjjahalgae.domain.proof.enums.ProofStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 인증 상세 response
 */
public record ProofDetailResponse(
   List<String> imageKeys,              // 인증 이미지들 1 번부터 순서대로 넣어서 보낼 예정
   String comment,                      // 코멘트 (없으면 null)
   ProofStatus status,                  // 인증 상태 (승인/거절)
   LocalDateTime createdAt,             // 몇 일자 인증인지
   boolean reProof,                     // 재인증 여부, 재인증 = true 원본 = false
//   List<FeedbackResponse> feedbacks   // 피드백들
   long proofId                         // 인증 id
) {}
