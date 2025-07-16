package me.jinjjahalgae.domain.proof.usecase.create.proof;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.notification.enums.NotificationType;
import me.jinjjahalgae.domain.notification.usecase.listener.event.NotificationEvent;
import me.jinjjahalgae.domain.proof.entities.Proof;
import me.jinjjahalgae.domain.proof.entities.ProofImage;
import me.jinjjahalgae.domain.proof.mapper.ProofImageMapper;
import me.jinjjahalgae.domain.proof.mapper.ProofMapper;
import me.jinjjahalgae.domain.proof.repository.ProofImageRepository;
import me.jinjjahalgae.domain.proof.repository.ProofRepository;
import me.jinjjahalgae.domain.proof.usecase.create.common.ProofCreateRequest;
import me.jinjjahalgae.global.exception.ErrorCode;
import me.jinjjahalgae.global.util.UtcDateTimeUtil;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateProofUseCaseImpl implements CreateProofUseCase {
    private final ProofRepository proofRepository;
    private final ProofImageRepository proofImageRepository;
    private final ContractRepository contractRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void execute(ProofCreateRequest request, Long contractId, Long userId) {
        Contract contract = contractRepository.findByIdWithUser(contractId)
                .orElseThrow(() -> ErrorCode.CONTRACT_NOT_FOUND.domainException(contractId + "에 해당하는 계약이 존재하지 않습니다."));

        // 유저의 계약이 아닐 경우 예외
        if (!isUserContract(contract, userId)) {
            throw ErrorCode.ACCESS_DENIED.domainException("계약에 대한 접근 권한이 없습니다.");
        }

        // 진행중이 아니라면
        if (!isInProgress(contract)) {
            throw ErrorCode.CONTRACT_MUST_IN_PROGRESS.domainException("계약 진행중이 아닙니다.");
        }

        // 이미지가 1장도 없는 경우 예외 발생
        if(request.firstImageKey() == null) {
            throw ErrorCode.IMAGE_REQUIRED.domainException("이미지가 존재하지 않습니다.");
        }

        boolean isProofExist = todayProofExist(contractId);

        // 원본 인증 요청인데 오늘자 인증이 이미 존재하는 경우
        if(isProofExist) {
            throw ErrorCode.PROOF_ALREADY_EXISTS.domainException("오늘자 인증이 이미 존재합니다.");
        }

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

        eventPublisher.publishEvent(new NotificationEvent(
                NotificationType.PROOF_ADDED,
                contractId,
                userId
        ));
    }

    // 계약이 진행중인지 확인하는 메서드
    private boolean isInProgress(Contract contract) {
        return contract.getStatus() == ContractStatus.IN_PROGRESS;
    }

    // 유저의 계약인지 확인하는 메서드
    private boolean isUserContract(Contract contract, Long userId) {
        return contract.getUser().getId().equals(userId);
    }

    // 계약에 오늘자 인증이 존재하는 지 확인하는 메서드
    private boolean todayProofExist(Long contractId) {
        LocalDate today = UtcDateTimeUtil.nowAsLocalDate();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();
        return proofRepository.existsByContractIdAndCreatedAtToday(contractId, startOfDay, endOfDay);
    }
}
