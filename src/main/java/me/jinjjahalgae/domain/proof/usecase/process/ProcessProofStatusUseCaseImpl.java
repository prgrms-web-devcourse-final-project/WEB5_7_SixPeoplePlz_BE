package me.jinjjahalgae.domain.proof.usecase.process;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jinjjahalgae.domain.feedback.entity.Feedback;
import me.jinjjahalgae.domain.feedback.repository.FeedbackRepository;
import me.jinjjahalgae.domain.proof.entities.Proof;
import me.jinjjahalgae.domain.proof.repository.ProofRepository;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessProofStatusUseCaseImpl implements ProcessProofStatusUseCase {
    private final ProofRepository proofRepository;
    private final FeedbackRepository feedbackRepository;


    @Override
    @Transactional
    public void execute(Long proofId) {
        try {
            // 존재하는 인증인지 검증
            Proof proof = proofRepository.findById(proofId).orElseThrow(()->
                    ErrorCode.PROOF_NOT_FOUND.domainException("인증을 찾을 수 없음: " + proofId)
            );

            // 인증에 관련된 피드백들을 가져와서
            List<Feedback> feedbacks = feedbackRepository.findByProofId(proof.getId());

            // 계산해서 처리
            proof.processFeedbackResult(feedbacks);

            // TODO 인증 처리 완료 알림 이벤트 발행
        } catch (Exception e) {
            log.error("인증 처리 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}
