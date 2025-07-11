package me.jinjjahalgae.domain.feedback.usecase.create;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.feedback.entity.Feedback;
import me.jinjjahalgae.domain.feedback.mapper.FeedbackMapper;
import me.jinjjahalgae.domain.feedback.repository.FeedbackRepository;
import me.jinjjahalgae.domain.feedback.usecase.create.dto.CreateFeedbackRequest;
import me.jinjjahalgae.domain.participation.enums.Role;
import me.jinjjahalgae.domain.participation.repository.ParticipationRepository;
import me.jinjjahalgae.domain.proof.entities.Proof;
import me.jinjjahalgae.domain.proof.event.HandleProofEvent;
import me.jinjjahalgae.domain.proof.repository.ProofRepository;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateFeedbackUseCaseImpl implements CreateFeedbackUseCase {
    private final FeedbackRepository feedbackRepository;
    private final ParticipationRepository participationRepository;
    private final ProofRepository proofRepository;

    private final ApplicationEventPublisher eventPublisher;

    /**
     * 감독자가 특정 인증에 대한 피드백을 생성합니다
     */
    @Override
    @Transactional
    public Void execute(Long userId, CreateFeedbackRequest req) {
        // 인증 유효성 검사 (존재하지 않는 인증이면 예외)
        Proof proof = proofRepository.findById(req.proofId())
                .orElseThrow(() -> ErrorCode.PROOF_NOT_FOUND.domainException("존재하지 않는 인증입니다.proofId=" + req.proofId()));

        // '포기않고' 해당 계약에 참가중인 '감독자'가 아니면 예외
        if (!hasSupervisorAuthority(userId, proof.getContractId())) {
            throw ErrorCode.PERMISSION_DENIED.domainException("감독자 권한이 없습니다.");
        }

        // 이미 피드백을 준 인증이면 예외
        boolean isduplicated = feedbackRepository.existsByProofIdAndUserId(proof.getId(), userId);

        if (isduplicated) {
            throw ErrorCode.FEEDBACK_ALREADY_EXISTS.domainException("이미 해당 인증에 피드백을 남겼습니다.");
        }

        // 새로운 피드백 생성
        Feedback feedback = FeedbackMapper.toEntity(userId, req, proof);

        feedbackRepository.save(feedback);

        // Proof의 checked_superviors + 1
        boolean isAllSupervisorsChecked = proof.increaseCheckedSupervisors(1);

        // 모든 감독자가 피드백을 남겼다면 인증 처리를 위한 이벤트 발행
        if (isAllSupervisorsChecked) {
            eventPublisher.publishEvent(new HandleProofEvent(proof.getId()));
        }

        return null;
    }

    // 유효한 감독자 권한이 존재하는지 검사
    private boolean hasSupervisorAuthority(Long userId, Long contractId) {
        return participationRepository.existsByContractIdAndUserIdAndRoleAndValid(
                contractId,
                userId,
                Role.SUPERVISOR,
                true
        );
    }
}
