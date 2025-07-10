package me.jinjjahalgae.domain.participation.usecase;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.participation.entity.Participation;
import me.jinjjahalgae.domain.participation.enums.Role;
import me.jinjjahalgae.domain.participation.usecase.interfaces.PatchSupervisorParticipationUseCase;
import me.jinjjahalgae.domain.user.User;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PatchSupervisorParticipationUseCaseImpl implements PatchSupervisorParticipationUseCase {

    private final ContractRepository contractRepository;

    @Override
    @Transactional
    public void execute(Long contractId, User user) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> ErrorCode.CONTRACT_NOT_FOUND.serviceException("존재하지 않는 계약 입니다. id =" + contractId));

        // 감독자 정보 조회
        // 감독자로 있지 않은 계약의 감독 중도 포기 요청을 한 경우 예외 반환
        Participation supervisorParticipation = contract.getParticipations()
                .stream()
                .filter(p -> p.getUser().equals(user) && p.getRole() == Role.SUPERVISOR)
                .findFirst()
                .orElseThrow(() -> ErrorCode.SUPERVISOR_PARTICIPATION_NOT_FOUND.serviceException("해당 계약에 감독으로 참여하고 있지 않습니다."));

        // 계약 진행중이 아닌데 감독 중도 포기(계약 진행 중 감독 포기)를 요청 한 경우
        if(contract.getStatus() != ContractStatus.IN_PROGRESS) {
            throw ErrorCode.CANNOT_ABANDON_PARTICIPATION_UNLESS_IN_PROGRESS.serviceException("계약 진행중에만 감독을 중도 포기할 수 있습니다.");
        }

        // 참여 여부를 false로 변경
        supervisorParticipation.invalidate();

        // 총 감독자 수 1 감소
        contract.decrementTotalSupervisor();
    }

}
