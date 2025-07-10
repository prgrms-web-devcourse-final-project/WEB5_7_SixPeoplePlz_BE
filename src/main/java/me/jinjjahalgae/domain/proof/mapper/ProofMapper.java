package me.jinjjahalgae.domain.proof.mapper;

import me.jinjjahalgae.domain.feedback.dto.FeedbackResponse;
import me.jinjjahalgae.domain.feedback.enums.FeedbackStatus;
import me.jinjjahalgae.domain.feedback.mapper.FeedbackMapper;
import me.jinjjahalgae.domain.proof.entities.Proof;
import me.jinjjahalgae.domain.proof.entities.ProofImage;
import me.jinjjahalgae.domain.proof.enums.ProofStatus;
import me.jinjjahalgae.domain.proof.usecase.get.await.ProofAwaitResponse;
import me.jinjjahalgae.domain.proof.usecase.getlist.contractorlist.ContractorProofListResponse;
import me.jinjjahalgae.domain.proof.usecase.getlist.contractorlist.ProofSimpleResponse;
import me.jinjjahalgae.domain.proof.usecase.get.detail.ProofDetailResponse;
import me.jinjjahalgae.domain.proof.usecase.get.recent.ProofRecentResponse;
import me.jinjjahalgae.domain.proof.usecase.getlist.supervisorlist.SupervisorProofListResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    /**
     * 계약자용 한 달 인증 목록 response 반환용
     * @param proof 인증
     * @param reProof 재인증 (없으면 null)
     * @return {@link ContractorProofListResponse}
     */
    public static ContractorProofListResponse toContractorListResponse(Proof proof, Proof reProof) {
        ProofSimpleResponse orgResponse = toSimpleResponse(proof);
        ProofSimpleResponse reProofResponse = null;

        if (reProof != null) {
            reProofResponse = toSimpleResponse(reProof);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = proof.getCreatedAt().format(formatter);

        LocalDateTime rejectedAt = null;
        if(proof.getStatus().equals(ProofStatus.REJECTED)) {
            rejectedAt = proof.getUpdatedAt();
        }

        return new ContractorProofListResponse(
                date,
                orgResponse,
                rejectedAt,
                reProofResponse
        );
    }

    /**
     * 감독자용 한 달 인증 목록 response 반환용
     * @param proof 인증
     * @param reProof 재인증 (없으면 null)
     * @param orgStatus 인증의 피드백 상태
     * @param reProofStatus 재인증의 피드백 상태 (재인증이 없으면 null)
     * @return {@link SupervisorProofListResponse}
     */
    public static SupervisorProofListResponse toSupervisorListResponse(Proof proof, Proof reProof, FeedbackStatus orgStatus, FeedbackStatus reProofStatus) {
        ProofSimpleResponse orgResponse = toSimpleResponse(proof);
        ProofSimpleResponse reProofResponse = null;

        if (reProof != null) {
            reProofResponse = toSimpleResponse(reProof);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = proof.getCreatedAt().format(formatter);

        return new SupervisorProofListResponse(
                date,
                orgResponse,
                orgStatus,
                reProofResponse,
                reProofStatus
        );
    }


    /**
     * 한 달 인증 목록 내부 공통 필드 response 반환용
     * @param proof 인증
     * @return {@link ProofSimpleResponse}
     */
    private static ProofSimpleResponse toSimpleResponse(Proof proof) {
        String imageKey = proof.getProofImages().stream()
                .filter(img -> img.getIndex() == 1)
                .findFirst()
                .map(ProofImage::getImageKey)
                .orElse(null);

        return new ProofSimpleResponse(
                imageKey,
                proof.getStatus(),
                proof.getTotalSupervisors(),
                proof.getCheckedSupervisors(),
                proof.getId()
        );
    }
}

