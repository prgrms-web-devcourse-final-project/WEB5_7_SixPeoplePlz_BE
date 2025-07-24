package me.jinjjahalgae.domain.contract.usecase.update;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractType;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.contract.usecase.update.dto.ContractUpdateRequest;
import me.jinjjahalgae.domain.participation.entity.Participation;
import me.jinjjahalgae.domain.participation.enums.Role;
import me.jinjjahalgae.domain.participation.mapper.ParticipationMapper;
import me.jinjjahalgae.domain.user.User;
import me.jinjjahalgae.domain.user.UserRepository;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateContractUseCaseImpl implements UpdateContractUseCase {

    private final ContractRepository contractRepository;
    private final EntityManager entityManager;
    private final ParticipationMapper participationMapper;
    private final UserRepository userRepository;

    @Override
    public void execute(Long userId, Long contractId, ContractUpdateRequest request) {
        try {
            // 계약 조회
            Contract contract = contractRepository.findByIdWithUser(contractId)
                    .orElseThrow(() -> ErrorCode.CONTRACT_NOT_FOUND.domainException("존재하지 않는 계약입니다."));

            // 계약자 권한 검증
            contract.validateContractor(userId);

            //감독자 서명이 있는가?
            contract.validateUpdatable();

            //기존 계약자 서명 삭제
            contract.removeContractorSignature();

            //계약 수정 진행
            contract.update(
                    request.title(),
                    request.goal(),
                    request.penalty(),
                    request.reward(),
                    request.totalProof(),
                    request.oneOff(),
                    request.startDate(),
                    request.endDate(),
                    ContractType.valueOf(request.type())
            );

            // 날짜 유효성 검사
            contract.validateDates();

            //총 인증 횟수는 계약일 수를 넘을 수 없으며 단발성이라면 총 인증 횟수 1인지도 검사
            contract.validateTotalProof();

            User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                    .orElseThrow(() -> ErrorCode.USER_NOT_FOUND.domainException("존재하지 않는 유저입니다."));

            //새로운 서명 추가
            Participation newContractorSignature = participationMapper.toEntity(
                    contract, user, request.signatureImageKey(), Role.CONTRACTOR, true
            );
            contract.addContractorSignature(newContractorSignature);

            entityManager.flush();

        } catch (OptimisticLockingFailureException e) {
            throw ErrorCode.CONTRACT_STATUS_CONFLICT.serviceException(
                    "계약 상태 변경 중 충돌이 발생했습니다. 다시 시도해주세요.");
        }
    }
}
