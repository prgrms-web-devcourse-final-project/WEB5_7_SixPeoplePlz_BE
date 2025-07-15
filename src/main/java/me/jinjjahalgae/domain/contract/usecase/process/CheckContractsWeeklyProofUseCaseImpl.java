//package me.jinjjahalgae.domain.contract.usecase.process;
//
//import lombok.RequiredArgsConstructor;
//import me.jinjjahalgae.domain.contract.entity.Contract;
//import me.jinjjahalgae.domain.contract.enums.ContractStatus;
//import me.jinjjahalgae.domain.contract.repository.ContractRepository;
//import me.jinjjahalgae.domain.proof.entities.Proof;
//import me.jinjjahalgae.domain.proof.enums.ProofStatus;
//import me.jinjjahalgae.domain.proof.repository.ProofRepository;
//import me.jinjjahalgae.global.storage.redis.usecase.invite.bulk.BulkDeleteInviteInfoUseCase;
//import me.jinjjahalgae.global.storage.redis.usecase.invite.get.GetJoinedSupervisorsUseCase;
//import org.springframework.context.ApplicationEventPublisher;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.temporal.ChronoUnit;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class CheckContractsWeeklyProofUseCaseImpl implements CheckContractsWeeklyProofUseCase {
//
//    private final ContractRepository contractRepository;
//    private final ProofRepository proofRepository;
//
//    @Override
//    @Transactional
//    public void execute() {
//        LocalDate today = LocalDate.now();
//        List<Contract> progressingContracts = contractRepository.findByStatus(ContractStatus.IN_PROGRESS);
//
//        if (progressingContracts.isEmpty()) return;
//
//        for (Contract contract : progressingContracts) {
//            long daysPassed = ChronoUnit.DAYS.between(contract.getStartDate().toLocalDate(), today);
//
//            // (7일 + 3일)이 지난 후 이전 7일에 대한 점검 수행
//            // 10일째 되는 날 -> 1~7일차 점검, 17일째 되는 날 -> 8~14일차 점검
//            if (daysPassed >= (7 + 3) && (daysPassed - 3) % 7 == 0) {
//                // n주차 계산
//                long week = (daysPassed - 3) / 7;
//
//                // 점검할 주의 시작일과 종료일 계산
//                LocalDateTime startOfWeek = contract.getStartDate().toLocalDate().plusDays((week - 1) * 7).atStartOfDay();
//                LocalDateTime endOfWeek = startOfWeek.plusDays(7).minusNanos(1);
//
//                // 주에 생성된 원본 인증들을 모두 조회
//                List<Proof> originalProofs = proofRepository.findOriginalProofsBetween(contract.getId(), startOfWeek, endOfWeek);
//
//                // 인증이 없으면 바로 다음 계약으로
//                if (originalProofs.isEmpty()) {
//                    contract.recordWeeklyFailure(contract.getProofPerWeek());
//                    continue;
//                }
//
//                List<Long> originalProofIds = originalProofs.stream().map(Proof::getId).toList();
//                List<Proof> reProofs = proofRepository.findReProofsByOriginalProofIds(originalProofIds);
//
//                int finalSuccessCount = 0;
//                for (Proof original : originalProofs) {
//                    // 전체 재인증 목록에서 현재 원본 인증에 해당하는 재인증을 찾음
//                    Optional<Proof> reProofOptional = reProofs.stream()
//                            .filter(rp -> original.getId().equals(rp.getProofId()))
//                            .findFirst();
//
//                    if (reProofOptional.isPresent()) {
//                        // 재인증이 승인된 경우 성공
//                        if (reProofOptional.get().getStatus() == ProofStatus.APPROVED) {
//                            finalSuccessCount++;
//                        }
//                    } else {
//                        // 재인증이 없고 원본 인증이 승인된 경우 성공
//                        if (original.getStatus() == ProofStatus.APPROVED) {
//                            finalSuccessCount++;
//                        }
//                    }
//                }
//
//                // 주간 필수 인증 횟수와 비교해 실패 처리
//                if (finalSuccessCount < contract.getProofPerWeek()) {
//                    contract.recordWeeklyFailure(contract.getProofPerWeek() - finalSuccessCount);
//                }
//            }
//        }
//    }
//}
