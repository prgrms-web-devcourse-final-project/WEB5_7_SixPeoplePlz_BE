package me.jinjjahalgae.domain.proof.usecase.getcontractorprooflist;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.proof.entities.Proof;
import me.jinjjahalgae.domain.proof.mapper.ProofMapper;
import me.jinjjahalgae.domain.proof.repository.ProofRepository;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetContractorProofListUseCaseImpl implements GetContractorProofListUseCase {

    private final ProofRepository proofRepository;
    private final ContractRepository contractRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ContractorProofListResponse> execute(Long contractId, int year, int month, Long userId) {
        // 유저의 계약인지 확인
        boolean isUserContract = contractRepository.existsByIdAndUserId(contractId, userId);

        // 유저의 계약이 아닐 경우 예외
        if (!isUserContract) {
            throw ErrorCode.ACCESS_DENIED.domainException("계약에 대한 접근 권한이 없습니다.");
        }

        // 달의 시작일 00:00:00
        LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0);

        // 달의 마지막 날 23:59:59.999999999
        LocalDateTime endDate = LocalDateTime.of(year, month, YearMonth.of(year, month).lengthOfMonth(), 23, 59, 59, 999999999);

        // 한 달에 해당하는 원본 인증 id들
        List<Long> proofIds = proofRepository.findOriginalProofIdsByMonth(contractId, startDate, endDate);

        // 모든 원본 인증들
        List<Proof> proofs = proofRepository.findProofsWithProofImagesByIds(proofIds);

        // 입력 받은 달에 해당하는 모든 재인증 id들
        List<Long> reProofIds = proofRepository.findReProofIdsByMonth(contractId, startDate, endDate);

        // 모든 재인증 객체들
        List<Proof> reProofs = proofRepository.findProofsWithProofImagesByIds(reProofIds);

        // 원본 인증 id를 기반으로 재인증을 Map으로 매핑
        Map<Long, Proof> reProofMap = reProofs.stream()
                .collect(Collectors.toMap(Proof::getProofId, Function.identity()));

        // 인증과 재인증을 하나의 응답으로 매핑
        return proofs.stream()
                .map(org -> {
                    Proof reProof = reProofMap.get(org.getId());
                    return ProofMapper.toContractorListResponse(org, reProof);
                })
                .toList();
    }
}
