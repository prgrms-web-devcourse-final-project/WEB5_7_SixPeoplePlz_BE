package me.jinjjahalgae.domain.contract.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.notification.enums.NotificationType;
import me.jinjjahalgae.domain.notification.usecase.listener.event.NotificationEvent;
import me.jinjjahalgae.domain.proof.entities.Proof;
import me.jinjjahalgae.domain.proof.enums.ProofStatus;
import me.jinjjahalgae.domain.proof.repository.ProofRepository;
import me.jinjjahalgae.global.storage.redis.usecase.invite.bulk.BulkDeleteInviteInfoUseCase;
import me.jinjjahalgae.global.storage.redis.usecase.invite.get.GetJoinedSupervisorsUseCase;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ContractScheduler {

    private final ContractRepository contractRepository;
    private final ProofRepository proofRepository;
    private final BulkDeleteInviteInfoUseCase bulkdeleteInviteInfoUseCase;
    private final GetJoinedSupervisorsUseCase getJoinedSupervisorsUseCase;
    private final ApplicationEventPublisher eventPublisher;

    // 계약 시작 스케줄러
    @Scheduled(cron = "0 1 0 * * *")
    @Transactional
    public void startContracts() {
        LocalDate today = LocalDate.now();
        List<Contract> pendingContracts = contractRepository.findByStatusAndStartDateOn(ContractStatus.PENDING, today);

        if (pendingContracts.isEmpty()) return;

        Map<Boolean, List<Contract>> pendingContractsBySupervisors = pendingContracts.stream()
                .collect(Collectors.partitioningBy(
                        contract -> getJoinedSupervisorsUseCase.execute(contract.getId()) > 0
                ));

        List<Contract> startContracts = pendingContractsBySupervisors.get(true);
        List<Contract> deleteContracts = pendingContractsBySupervisors.get(false);

        // 계약 시작 처리
        for (Contract contract : startContracts) {
            int joinedSupervisors = getJoinedSupervisorsUseCase.execute(contract.getId());
            contract.start(joinedSupervisors);

            eventPublisher.publishEvent(new NotificationEvent(
                            NotificationType.CONTRACT_STARTED,
                            contract.getId(),
                            contract.getUser().getId()
                    )
            );
        }

        // 감독자 부족 계약 벌크 삭제
        if (!deleteContracts.isEmpty()) {
            List<Long> deleteContractIds = deleteContracts.stream().map(Contract::getId).toList();
            contractRepository.deleteAllByIdInBatch(deleteContractIds);
            
            deleteContracts.forEach(contract ->
                    eventPublisher.publishEvent(new NotificationEvent(
                            NotificationType.CONTRACT_AUTO_DELETED,
                            contract.getId(),
                            contract.getUser().getId()
                    ))
            );
        }

        // 처리된 계약의 redis 정보 일괄 삭제
        List<Long> allProcessedIds = pendingContracts.stream().map(Contract::getId).toList();
        bulkdeleteInviteInfoUseCase.execute(allProcessedIds);
    }

    // 주간 인증 상황을 점검하는 스케줄러
    @Scheduled(cron = "0 50 23 * * *")
    @Transactional
    public void checkProgressingContracts() {
        LocalDate today = LocalDate.now();
        List<Contract> progressingContracts = contractRepository.findByStatus(ContractStatus.IN_PROGRESS);

        if (progressingContracts.isEmpty()) return;

        for (Contract contract : progressingContracts) {
            long daysPassed = ChronoUnit.DAYS.between(contract.getStartDate().toLocalDate(), today);

            // (7일 + 3일)이 지난 후 이전 7일에 대한 점검 수행
            // 10일째 되는 날 -> 1~7일차 점검, 17일째 되는 날 -> 8~14일차 점검
            if (daysPassed >= (7 + 3) && (daysPassed - 3 + 1) % 7 == 0) {
                // n주차 계산
                long week = (daysPassed - 3 + 1) / 7;

                // 점검할 주의 시작일과 종료일 계산
                LocalDateTime startOfWeek = contract.getStartDate().toLocalDate().plusDays((week - 1) * 7).atStartOfDay();
                LocalDateTime endOfWeek = startOfWeek.plusDays(7).minusNanos(1);

                // 주에 생성된 원본 인증들을 모두 조회
                List<Proof> originalProofs = proofRepository.findOriginalProofsBetween(contract.getId(), startOfWeek, endOfWeek);

                // 인증이 없으면 바로 다음 계약으로
                if (originalProofs.isEmpty()) {
                    contract.recordWeeklyFailure(contract.getProofPerWeek());
                    continue;
                }

                List<Long> originalProofIds = originalProofs.stream().map(Proof::getId).toList();
                List<Proof> reProofs = proofRepository.findReProofsByOriginalProofIds(originalProofIds);

                int finalSuccessCount = 0;
                for (Proof original : originalProofs) {
                    // 전체 재인증 목록에서 현재 원본 인증에 해당하는 재인증을 찾음
                    Optional<Proof> reProofOptional = reProofs.stream()
                            .filter(rp -> original.getId().equals(rp.getProofId()))
                            .findFirst();

                    if (reProofOptional.isPresent()) {
                        // 재인증이 승인된 경우 성공
                        if (reProofOptional.get().getStatus() == ProofStatus.APPROVED) {
                            finalSuccessCount++;
                        }
                    } else {
                        // 재인증이 없고 원본 인증이 승인된 경우 성공
                        if (original.getStatus() == ProofStatus.APPROVED) {
                            finalSuccessCount++;
                        }
                    }
                }

                // 주간 필수 인증 횟수와 비교해 실패 처리
                if (finalSuccessCount < contract.getProofPerWeek()) {
                    contract.recordWeeklyFailure(contract.getProofPerWeek() - finalSuccessCount);
                }
            }
        }
    }

    // 계약 종료 스케줄러
    @Scheduled(cron = "0 59 23 * * *")
    @Transactional
    public void endContracts() {
        // 대기중인 인증이 "없는" 단발이 아닌 계약을 결과 대기중 상태로 변환
        LocalDate today = LocalDate.now();
        contractRepository.bulkUpdateCompletedContractsToWait(today);

        List<Contract> waitingContracts = contractRepository.findByStatus(ContractStatus.WAIT_RESULT);

        if (waitingContracts.isEmpty()) return;

        // 마지막 주차 실패 횟수 업데이트
        for (Contract contract : waitingContracts) {
            long totalDays = ChronoUnit.DAYS.between(contract.getStartDate().toLocalDate(), contract.getEndDate().toLocalDate()) + 1;

            if (totalDays % 7 > 0) { // 마지막 주가 7일 미만일 경우
                // 마지막 주차 시작일 계산
                long totalWeeks = totalDays / 7;
                LocalDateTime startOfLastWeek = contract.getStartDate().plusDays(totalWeeks * 7);

                // 마지막 주차의 인증 승인 횟수 계산
                int successCount = proofRepository.countByContractIdAndStatusAndCreatedAtBetween(
                        contract.getId(), ProofStatus.APPROVED, startOfLastWeek, contract.getEndDate());
                if (successCount < contract.getProofPerWeek()) {
                    contract.recordWeeklyFailure(contract.getProofPerWeek() - successCount);
                }
            }
        }

        Map<Boolean, List<Contract>> partitionedContractsByResult = waitingContracts.stream()
                .collect(Collectors.partitioningBy(
                        contract -> contract.getLife() >= contract.getCurrentFail()
                ));

        List<Contract> successContracts = partitionedContractsByResult.get(true);
        List<Contract> failContracts = partitionedContractsByResult.get(false);

        // 성공 계약 벌크 업데이트
        if (!successContracts.isEmpty()) {
            List<Long> successIds = successContracts.stream().map(Contract::getId).toList();
            contractRepository.bulkUpdateStatus(successIds, ContractStatus.COMPLETED);

            successContracts.forEach(contract ->
                    eventPublisher.publishEvent(new NotificationEvent(
                            NotificationType.CONTRACT_ENDED_SUCCESS,
                            contract.getId(),
                            contract.getUser().getId()
                    ))
            );
        }

        // 실패 계약 벌크 업데이트
        if (!failContracts.isEmpty()) {
            List<Long> failIds = failContracts.stream().map(Contract::getId).toList();
            contractRepository.bulkUpdateStatus(failIds, ContractStatus.FAILED);

            failContracts.forEach(contract ->
                    eventPublisher.publishEvent(new NotificationEvent(
                            NotificationType.CONTRACT_ENDED_FAIL,
                            contract.getId(),
                            contract.getUser().getId()
                    ))
            );
        }

        // 대기중인 인증이 "있는" 단발이 아닌 계약을 결과 대기중 상태로 변환
        contractRepository.bulkUpdateApprovePendingContractsToWait(today);
    }
}
