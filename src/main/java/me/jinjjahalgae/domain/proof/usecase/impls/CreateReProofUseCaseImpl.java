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
import me.jinjjahalgae.domain.proof.usecase.interfaces.CreateReProofUseCase;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateReProofUseCaseImpl implements CreateReProofUseCase {
    private final ProofRepository proofRepository;
    private final ProofImageRepository proofImageRepository;
    private final ContractRepository contractRepository;

    @Override
    @Transactional
    public void execute(ProofCreateRequest request, Long proofId) {
        // 이미지가 1장도 없는 경우 예외 발생
        if(request.firstImageKey() == null) {
            throw ErrorCode.IMAGE_REQUIRED.domainException("이미지가 존재하지 않습니다.");
        }

        // 원본 인증 조회 - 원본 인증이 존재하는 지 검증 겸용
        Proof proof = proofRepository.findById(proofId)
                .orElseThrow(() -> ErrorCode.PROOF_NOT_FOUND.domainException(proofId + "에 대한 인증이 존재하지 않습니다."));

        // 계약 조회 - 계약이 존재하는 지 검증 겸용
        Long contractId = proof.getContractId();
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> ErrorCode.CONTRACT_NOT_FOUND.domainException(contractId + "에 대한 계약이 존재하지 않습니다."));

        // 해당 계약에 대해 오늘자 재인증이 존재하는 지 검증
        boolean isReProofExist = todayReProofExist(contract.getId());
        if(isReProofExist) {
            throw ErrorCode.REPROOF_ALREADY_EXISTS.domainException("오늘자 재인증이 이미 존재합니다.");
        }

        // 재인증 저장
        Proof reProof = ProofMapper.toEntity(request.comment(), contract.getTotalSupervisor(), contract.getId(), proofId);
        Proof savedReproof = proofRepository.save(reProof);

        // 1 번째 이미지 저장
        ProofImage firstImage = ProofImageMapper.toEntity(request.firstImageKey(), 1);
        ProofImage savedFirstImage = proofImageRepository.save(firstImage);
        savedReproof.addProofImage(savedFirstImage);

        // 2 번째 이미지가 존재하는 경우 저장
        if(request.secondImageKey() != null) {
            ProofImage secondImage = ProofImageMapper.toEntity(request.secondImageKey(), 2);
            ProofImage savedSecondImage = proofImageRepository.save(secondImage);
            savedReproof.addProofImage(savedSecondImage);
        }

        // 3 번째 이미지가 존재하는 경우 저장
        if(request.thirdImageKey() != null) {
            ProofImage thirdImage = ProofImageMapper.toEntity(request.thirdImageKey(), 3);
            ProofImage savedThirdImage = proofImageRepository.save(thirdImage);
            savedReproof.addProofImage(savedThirdImage);
        }
    }

    // 계약에 오늘자 재인증이 존재하는 지 확인하는 메서드
    private boolean todayReProofExist(Long contractId) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();
        return proofRepository.existsReProofByContractIdAndCreatedAtToday(contractId, startOfDay, endOfDay);
    }
}
