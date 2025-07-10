package me.jinjjahalgae.domain.proof.usecase.getrecentproof;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.proof.entities.Proof;
import me.jinjjahalgae.domain.proof.mapper.ProofMapper;
import me.jinjjahalgae.domain.proof.repository.ProofRepository;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetRecentProofUseCaseImpl implements GetRecentProofUseCase {

    private final ProofRepository proofRepository;
    private final ContractRepository contractRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProofRecentResponse> execute(Long contractId, Long userId) {
        // 유저의 계약인지 확인
        boolean isUserContract = contractRepository.existsByIdAndUserId(contractId, userId);

        // 유저의 계약이 아닐 경우 예외
        if (!isUserContract) {
            throw ErrorCode.ACCESS_DENIED.domainException("계약에 대한 접근 권한이 없습니다.");
        }

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
