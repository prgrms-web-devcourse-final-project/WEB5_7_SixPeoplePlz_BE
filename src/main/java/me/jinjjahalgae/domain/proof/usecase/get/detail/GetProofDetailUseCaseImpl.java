package me.jinjjahalgae.domain.proof.usecase.get.detail;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.feedback.entity.Feedback;
import me.jinjjahalgae.domain.participation.repository.ParticipationRepository;
import me.jinjjahalgae.domain.proof.entities.Proof;
import me.jinjjahalgae.domain.proof.mapper.ProofImageMapper;
import me.jinjjahalgae.domain.proof.mapper.ProofMapper;
import me.jinjjahalgae.domain.proof.repository.ProofRepository;
import me.jinjjahalgae.domain.proof.usecase.get.detail.dto.ProofDetailResponse;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetProofDetailUseCaseImpl implements GetProofDetailUseCase {
    private final ProofRepository proofRepository;
    private final ParticipationRepository participationRepository;

    @Override
    @Transactional(readOnly = true)
    public ProofDetailResponse execute(Long proofId, Long userId) {
        // 인증 조회 (피드백)
        Proof proofWithFeedback = proofRepository.findByIdWithFeedbacks(proofId)
                .orElseThrow(() -> ErrorCode.PROOF_NOT_FOUND.domainException(proofId + "에 대한 인증이 존재하지 않습니다."));

        Proof proofWithImage = proofRepository.findByIdWithProofImages(proofId)
                .orElseThrow(() -> ErrorCode.PROOF_NOT_FOUND.domainException(proofId + "에 대한 인증이 존재하지 않습니다."));

        // 유저가 계약의 참여자인지 확인
        boolean isUserParticipate = participationRepository.existsByContractIdAndUserId(proofWithImage.getContractId(), userId);

        // 유저가 계약의 참여자가 아닐 경우 예외
        if (!isUserParticipate) {
            throw ErrorCode.ACCESS_DENIED.domainException("계약에 대한 접근 권한이 없습니다.");
        }

        // 이미지 순서대로 key만 추출
        List<String> images = ProofImageMapper.toListResponse(proofWithImage.getProofImages());

        // 피드백 추출
        List<Feedback> feedbacks = proofWithFeedback.getFeedbacks();

        // 이미지와 인증 정보와 피드백을 합친 응답 생성
        return ProofMapper.toDetailResponse(proofWithFeedback, images, feedbacks);
    }
}
