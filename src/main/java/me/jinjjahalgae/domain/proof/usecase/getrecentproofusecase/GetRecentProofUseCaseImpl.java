package me.jinjjahalgae.domain.proof.usecase.getrecentproofusecase;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.proof.entities.Proof;
import me.jinjjahalgae.domain.proof.mapper.ProofMapper;
import me.jinjjahalgae.domain.proof.repository.ProofRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetRecentProofUseCaseImpl implements GetRecentProofUseCase {

    private final ProofRepository proofRepository;

    @Override
    public List<ProofRecentResponse> execute(Long contractId, Long userId) {
        // 최근 3개의 인증 id를 계약 id로 가져옴
        List<Long> proofIds = proofRepository.findProofIdsByContractId(contractId, PageRequest.of(0, 3));

        // 가져온 인증 id로 인증 객체를 가져옴
        List<Proof> proofs = proofRepository.findProofsWithProofImagesByIds(proofIds);

        // dto 변환
        return proofs.stream()
                .map(ProofMapper::toRecentResponse)
                .toList();
    }
}
