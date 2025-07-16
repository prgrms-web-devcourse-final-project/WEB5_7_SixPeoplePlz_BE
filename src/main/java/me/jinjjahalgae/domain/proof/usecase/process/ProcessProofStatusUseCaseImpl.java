package me.jinjjahalgae.domain.proof.usecase.process;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.feedback.entity.Feedback;
import me.jinjjahalgae.domain.feedback.repository.FeedbackRepository;
import me.jinjjahalgae.domain.notification.enums.NotificationType;
import me.jinjjahalgae.domain.notification.usecase.listener.event.NotificationEvent;
import me.jinjjahalgae.domain.proof.entities.Proof;
import me.jinjjahalgae.domain.proof.repository.ProofRepository;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessProofStatusUseCaseImpl implements ProcessProofStatusUseCase {
    private final ProofRepository proofRepository;
    private final FeedbackRepository feedbackRepository;
    private final ContractRepository contractRepository;

    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void execute(Long proofId) {
        try {
            // 존재하는 인증인지 검증
            Proof proof = proofRepository.findById(proofId).orElseThrow(()->
                    ErrorCode.PROOF_NOT_FOUND.domainException("인증을 찾을 수 없음: " + proofId)
            );

            // proof의 contractId로 contract 조회
            Contract contract = contractRepository.findById(proof.getContractId()).orElseThrow(()->
                    ErrorCode.CONTRACT_NOT_FOUND.domainException("계약을 찾을 수 없음: " + proof.getContractId())
            );

            // 인증에 관련된 피드백들을 가져와서
            List<Feedback> feedbacks = feedbackRepository.findByProofId(proof.getId());

            // 계산해서 처리
            proof.processFeedbackResult(feedbacks);

            // 인증 계산 결과에 따른 처리
            if(proof.isApproved()) {
                // 승인된 경우 계약의 현재 인증 횟수 증가
                contract.incrementCurrentProof();

                // 인증 승인 알림 전송
                eventPublisher.publishEvent(new NotificationEvent(NotificationType.PROOF_ACCEPTED, contract.getId(), contract.getUser().getId()));
            }else{
                // 인증 거절 알림 전송
                eventPublisher.publishEvent(new NotificationEvent(NotificationType.PROOF_REJECTED, contract.getId(), contract.getUser().getId()));
            }

            // "단발 계약일 때" 인증 결과에 따라 즉시 계약 상태 처리 + 알림 전송
            if (contract.isOneOff() && contract.isInProgress()) {
                if(proof.isApproved()) {
                    contract.complete();

                    eventPublisher.publishEvent(new NotificationEvent(NotificationType.CONTRACT_ENDED_SUCCESS, contract.getId(), contract.getUser().getId()));
                } else {
                    contract.fail();
                    
                    eventPublisher.publishEvent(new NotificationEvent(NotificationType.CONTRACT_ENDED_FAIL, contract.getId(), contract.getUser().getId()));
                }
            }
        } catch (Exception e) {
            log.error("인증 처리 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}
