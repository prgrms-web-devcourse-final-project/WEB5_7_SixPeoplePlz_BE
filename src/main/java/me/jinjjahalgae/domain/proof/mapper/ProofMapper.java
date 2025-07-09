package me.jinjjahalgae.domain.proof.mapper;

import me.jinjjahalgae.domain.feedback.dto.FeedbackResponse;
import me.jinjjahalgae.domain.feedback.mapper.FeedbackMapper;
import me.jinjjahalgae.domain.proof.dto.response.ProofAwaitResponse;
import me.jinjjahalgae.domain.proof.dto.response.ProofDetailResponse;
import me.jinjjahalgae.domain.proof.dto.response.ProofRecentResponse;
import me.jinjjahalgae.domain.proof.entities.Proof;
import me.jinjjahalgae.domain.proof.entities.ProofImage;
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
        List<FeedbackResponse> feedbacks = proof.getFeedbacks().stream()
                .map(FeedbackMapper::toResponse)
                .toList();

        return new ProofDetailResponse(
                images,
                proof.getComment(),
                proof.getStatus(),
                proof.getCreatedAt(),
                proof.getProofId() != null,
                feedbacks,
                proof.getId()
                );
    }

    /**
     * 최근 인증 조회 response 변환용
     * @param proof 인증
     * @return {@link ProofRecentResponse}
     */
    public static ProofRecentResponse toRecentResponse(Proof proof) {
        String imageKey = proof.getProofImages().stream()
                .filter(img -> img.getIndex() == 1)
                .findFirst()
                .map(ProofImage::getImageKey)
                .orElse(null);

        return new ProofRecentResponse(
                imageKey,
                proof.getComment(),
                proof.getStatus(),
                proof.getCreatedAt(),
                proof.getProofId() != null,
                proof.getId()
        );
    }

    /**
     * 대기중인 인증 조회 response 변환용
     * @param proof 인증
     * @return {@link ProofAwaitResponse}
     */
    public static ProofAwaitResponse toAwaitResponse(Proof proof) {
        String imageKey = proof.getProofImages().stream()
                .filter(img -> img.getIndex() == 1)
                .findFirst()
                .map(ProofImage::getImageKey)
                .orElse(null);

        return new ProofAwaitResponse(
                imageKey,
                proof.getComment(),
                proof.getCreatedAt(),
                proof.getStatus(),
                proof.getProofId() != null,
                proof.getId()
        );
    }
}
