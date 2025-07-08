package me.jinjjahalgae.domain.proof.usecase.impls;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.proof.dto.response.ProofDetailResponse;
import me.jinjjahalgae.domain.proof.entities.Proof;
import me.jinjjahalgae.domain.proof.mapper.ProofImageMapper;
import me.jinjjahalgae.domain.proof.mapper.ProofMapper;
import me.jinjjahalgae.domain.proof.repository.ProofRepository;
import me.jinjjahalgae.domain.proof.usecase.interfaces.GetProofDetailUseCase;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetProofDetailUseCaseImpl implements GetProofDetailUseCase {
    private final ProofRepository proofRepository;

    @Override
    public ProofDetailResponse execute(Long proofId) {
        // 인증 조회
        Proof proof = proofRepository.findByIdWithProofImages(proofId)
                .orElseThrow(() -> ErrorCode.PROOF_NOT_FOUND.domainException(proofId + "에 대한 인증이 존재하지 않습니다."));

        // 이미지 순서대로 key만 추출
        List<String> images = ProofImageMapper.toListResponse(proof.getProofImages());

        // 이미지와 인증 정보를 합친 응답 생성
        return ProofMapper.toDetailResponse(proof, images);
    }
}
