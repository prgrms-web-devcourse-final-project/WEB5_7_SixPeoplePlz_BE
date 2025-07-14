package me.jinjjahalgae.domain.proof.usecase.schedule;


import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.feedback.entity.Feedback;
import me.jinjjahalgae.domain.feedback.enums.FeedbackStatus;
import me.jinjjahalgae.domain.feedback.repository.FeedbackRepository;
import me.jinjjahalgae.domain.proof.entities.Proof;
import me.jinjjahalgae.domain.proof.enums.ProofStatus;
import me.jinjjahalgae.domain.proof.repository.ProofRepository;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckExpiredProofUseCaseImpl implements CheckExpiredProofUseCase {

    private final ProofRepository proofRepository;
    private final FeedbackRepository feedbackRepository;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public void execute(LocalDateTime now) {
        LocalDateTime deadline = now.minusHours(24);

        int page = 0;
        int size = 50;
        Page<Proof> proofPage;
        List<Long> approvedProofIds = new ArrayList<>();
        List<Long> rejectedProofIds = new ArrayList<>();

        do {
            proofPage = proofRepository.findProofsPendingOver24Hours(
                    deadline,
                    PageRequest.of(page, size)
            );

            for (Proof proof : proofPage.getContent()) {
                // 1. 피드백이 없는 감독자에게 자동 승인 피드백 생성 (native 쿼리로 자동 insert)
                feedbackRepository.createAutoApprovalFeedbacks(proof.getId(), proof.getContractId(), now);

                // 2. 피드백이 없는 감독자까지 포함해 최종 승인/거절 개수 집계
                entityManager.clear();

                // 3. Proof와 피드백을 다시 조회 (최신 상태 반영)
                Proof refreshedProof = proofRepository.findByIdWithFeedbacks(proof.getId()).orElseThrow(
                        () ->ErrorCode.PROOF_NOT_FOUND.domainException(proof.getId() + "에 대한 인증이 존재하지 않습니다")
                );
                List<Feedback> feedbacks = refreshedProof.getFeedbacks(); // LAZY, batch size 적용
                long approvedCount = feedbacks.stream().filter(f -> f.getStatus() == FeedbackStatus.APPROVED).count();
                long rejectedCount = feedbacks.stream().filter(f -> f.getStatus() == FeedbackStatus.REJECTED).count();

                // 4. 승인/거절 비율에 따라 Proof 상태 결정
                if (approvedCount > rejectedCount) {
                    approvedProofIds.add(refreshedProof.getId());
                } else {
                    rejectedProofIds.add(refreshedProof.getId());
                }
            }

            page++;
        } while (proofPage.hasNext());

        // 5. Proof 상태 벌크 업데이트
        if (!approvedProofIds.isEmpty()) {
            proofRepository.updateProofStatus(approvedProofIds, ProofStatus.APPROVED);
        }
        if (!rejectedProofIds.isEmpty()) {
            proofRepository.updateProofStatus(rejectedProofIds, ProofStatus.REJECTED);
        }

    }
}
