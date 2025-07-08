package me.jinjjahalgae.domain.proof.dto.response;

import java.time.LocalDateTime;
/**
 * 계약자 달력 표시를 위한 response
 * 재인증 필드는 재인증이 존재하지 않는 경우 null
 */
public record ContractorProofListResponse(
        String date,                        // 달력 라우팅용 일 단위 매핑 (yyyy-MM-dd)

        ProofSimpleResponse originalProof,  // 원본 인증 데이터
        LocalDateTime rejectedAt,           // 재인증 요청 버튼 출력용

        ProofSimpleResponse reProof         // 재인증 데이터
) {}
