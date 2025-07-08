package me.jinjjahalgae.domain.proof.usecase.impls;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.proof.dto.request.ProofCreateRequest;
import me.jinjjahalgae.domain.proof.entities.Proof;
import me.jinjjahalgae.domain.proof.entities.ProofImage;
import me.jinjjahalgae.domain.proof.mapper.ProofImageMapper;
import me.jinjjahalgae.domain.proof.mapper.ProofMapper;
import me.jinjjahalgae.domain.proof.repository.ProofImageRepository;
import me.jinjjahalgae.domain.proof.repository.ProofRepository;
import me.jinjjahalgae.domain.proof.usecase.interfaces.ProofCreateUseCase;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProofCreateUseCaseImpl implements ProofCreateUseCase {
    private final ProofRepository proofRepository;
    private final ProofImageRepository proofImageRepository;
    private final ContractRepository contractRepository;

    @Override
    public void execute(ProofCreateRequest request, Long contractId) {
        // 이미지가 1장도 없는 경우 예외 발생
        if(request.firstImageKey() == null) {
            // ErrorCode.IMAGE_REQUIRED.domainException("이미지가 존재하지 않음");
        }

        boolean isProofExist = todayProofExist(contractId);

        // 원본 인증 요청인데 오늘자 인증이 이미 존재하는 경우
        if(isProofExist) {
            // ErrorCode.PROOF_ALREADY_EXISTS.domainException("원본 인증 요청인데 오늘자 인증이 이미 존재함");
        }

        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(// CONTRACT_NOT_FOUND
                );

        // 인증 생성
        Proof proof = ProofMapper.toEntity(request.comment(), contract.getTotalSupervisor(), contractId);
        Proof savedProof = proofRepository.save(proof);

        // 1 번째 이미지 저장
        ProofImage firstImage = ProofImageMapper.toEntity(request.firstImageKey(), 1);
        ProofImage savedFirstImage = proofImageRepository.save(firstImage);
        savedProof.addProofImage(savedFirstImage);

        // 2 번째 이미지가 존재하는 경우 저장
        if(request.secondImageKey() != null) {
            ProofImage secondImage = ProofImageMapper.toEntity(request.secondImageKey(), 2);
            ProofImage savedSecondImage = proofImageRepository.save(secondImage);
            savedProof.addProofImage(savedSecondImage);
        }

        // 3 번째 이미지가 존재하는 경우 저장
        if(request.thirdImageKey() != null) {
            ProofImage thirdImage = ProofImageMapper.toEntity(request.thirdImageKey(), 3);
            ProofImage savedThirdImage = proofImageRepository.save(thirdImage);
            savedProof.addProofImage(savedThirdImage);
        }
    }

    // 계약에 오늘자 인증이 존재하는 지 확인하는 메서드
    private boolean todayProofExist(Long contractId) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();
        return proofRepository.existsByContractIdAndCreatedAtToday(contractId, startOfDay, endOfDay);
    }
}
