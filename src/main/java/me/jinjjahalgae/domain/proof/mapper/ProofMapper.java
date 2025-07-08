package me.jinjjahalgae.domain.proof.mapper;

import me.jinjjahalgae.domain.proof.dto.response.ProofDetailResponse;
import me.jinjjahalgae.domain.proof.entities.Proof;
import me.jinjjahalgae.domain.proof.enums.ProofStatus;

import java.util.List;

public class ProofMapper {

    /**
     * 인증 요청 entity 변환용
     * @param comment           코멘트
     * @param totalSupervisors  총 감독자 수
     * @param contractId        계약 id
     * @return  {@link Proof}
     */
    public static Proof toEntity(String comment, int totalSupervisors, Long contractId) {
        return Proof.builder()
                .comment(comment)
                .totalSupervisors(totalSupervisors)
                .checkedSupervisors(0)
                .status(ProofStatus.APPROVE_PENDING)
                .contractId(contractId)
                .build();
    }

    /**
     * 재인증 요청 entity 변환용
     * @param comment           코멘트
     * @param totalSupervisors  총 감독자 수
     * @param contractId        계약 id
     * @param proofId           인증 id
     * @return  {@link Proof}
     */
    public static Proof toEntity(String comment, int totalSupervisors, Long contractId, Long proofId) {
        return Proof.builder()
                .comment(comment)
                .totalSupervisors(totalSupervisors)
                .checkedSupervisors(0)
                .status(ProofStatus.APPROVE_PENDING)
                .contractId(contractId)
                .proofId(proofId)
                .build();
    }

    /**
     * 인증 상세 조회 response 변환용
     * @param proof     변환할 인증 객체
     * @param images    인증에 들어있는 이미지 key 리스트
     * @return  {@link ProofDetailResponse}
     */
    public static ProofDetailResponse toDetailResponse(Proof proof, List<String> images) {
        return new ProofDetailResponse(
                images,
                proof.getComment(),
                proof.getStatus(),
                proof.getCreatedAt(),
                proof.getProofId() != null,
                proof.getId()
                );
    }
}
