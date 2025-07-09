package me.jinjjahalgae.domain.proof.usecase.getawaitproofusecase;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.participation.repository.ParticipationRepository;
import me.jinjjahalgae.domain.proof.entities.Proof;
import me.jinjjahalgae.domain.proof.mapper.ProofMapper;
import me.jinjjahalgae.domain.proof.repository.ProofRepository;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAwaitProofUseCaseImpl implements GetAwaitProofUseCase {

    private final ProofRepository proofRepository;
    private final ParticipationRepository participationRepository;

    @Override
    public List<ProofAwaitResponse> execute(Long contractId, Long userId) {
        // 유저가 계약의 참여자인지 확인
        boolean isUserParticipate = participationRepository.existsByContractIdAndUserId(contractId, userId);

        // 유저가 계약의 참여자가 아닐 경우 예외
        if (!isUserParticipate) {
            throw ErrorCode.ACCESS_DENIED.domainException("계약에 대한 접근 권한이 없습니다.");
        }

        // 인증이 대기 상태이고 감독자가 처리하지 않은 계약의 인증 id들을 조회
        List<Long> proofIds = proofRepository.findPendingProofIdsWithoutUserFeedback(contractId, userId);

        // 인증 id로 인증 이미지와 함께 조회
        List<Proof> proofs = proofRepository.findProofsWithProofImagesByIds(proofIds);

        // dto 응답 반환
        return proofs.stream()
                .map(ProofMapper::toAwaitResponse)
                .toList();
    }
}
