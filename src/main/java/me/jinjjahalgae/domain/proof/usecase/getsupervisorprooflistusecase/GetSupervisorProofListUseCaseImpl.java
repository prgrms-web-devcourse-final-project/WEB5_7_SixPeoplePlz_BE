package me.jinjjahalgae.domain.proof.usecase.getsupervisorprooflistusecase;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.feedback.entity.Feedback;
import me.jinjjahalgae.domain.feedback.enums.FeedbackStatus;
import me.jinjjahalgae.domain.feedback.repository.FeedbackRepository;
import me.jinjjahalgae.domain.proof.entities.Proof;
import me.jinjjahalgae.domain.proof.mapper.ProofMapper;
import me.jinjjahalgae.domain.proof.repository.ProofRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetSupervisorProofListUseCaseImpl implements GetSupervisorProofListUseCase {

    private final ProofRepository proofRepository;
    private final FeedbackRepository feedbackRepository;

    @Override
    public List<SupervisorProofListResponse> execute(Long contractId, int year, int month, Long userId) {
        // 달의 시작일 00:00:00
        LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0);

        // 달의 마지막 날 23:59:59.999999999
        LocalDateTime endDate = LocalDateTime.of(year, month, YearMonth.of(year, month).lengthOfMonth(), 23, 59, 59, 999999999);

        // 한 달에 해당하는 원본 인증 id들 (감독자가 피드백을 한 것만)
        List<Long> proofIds = proofRepository.findOriginalProofIdsByMonthForSupervisor(contractId, startDate, endDate, userId);

        // 감독자가 처리한 원본 인증들
        List<Proof> proofs = proofRepository.findProofsWithProofImagesByIds(proofIds);

        // 한 달에 해당하는 모든 재인증 id들 (감독자가 피드백을 한 것만)
        List<Long> reProofIds = proofRepository.findReProofIdsByMonthForSupervisor(contractId, startDate, endDate, userId);

        // 감독자가 처리한 재인등들
        List<Proof> reProofs = proofRepository.findProofsWithProofImagesByIds(reProofIds);

        // 원본 인증 id를 기반으로 재인증을 Map으로 매핑
        Map<Long, Proof> reProofMap = reProofs.stream()
                .collect(Collectors.toMap(Proof::getProofId, Function.identity()));

        // 감독자가 해당 계약에서 처리한 피드백들
        List<Feedback> feedbacks = feedbackRepository.findByContractIdAndUserId(contractId, userId);

        // 인증 id를 key로 피드백 상태를 Map에 매핑
        Map<Long, FeedbackStatus> feedbackMap = feedbacks.stream()
                .collect(Collectors.toMap(
                        feedback -> feedback.getProof().getId(),
                        Feedback::getStatus
                ));

        // 응답 dto 변환
        return proofs.stream()
                .map(org -> {
                    return getSupervisorProofListResponse(org, reProofMap, feedbackMap);
                })
                .toList();
    }

    private SupervisorProofListResponse getSupervisorProofListResponse(Proof org, Map<Long, Proof> reProofMap, Map<Long, FeedbackStatus> feedbackMap) {
        // 원본 인증 id로 얻은 재인증 (없으면 null)
        Proof reProof = reProofMap.get(org.getId());

        // 원본 인증 id로 얻은 피드백 상태
        FeedbackStatus orgFeedbackStatus = feedbackMap.get(org.getId());

        // 재인증 피드백 상태
        FeedbackStatus reProofFeedbackStatus = null;

        // 재인증이 아니라면 상태를 매핑
        if (reProof != null) {
            reProofFeedbackStatus = feedbackMap.get(reProof.getId());
        }
        return ProofMapper.toSupervisorListResponse(org, reProof, orgFeedbackStatus, reProofFeedbackStatus);
    }
}
