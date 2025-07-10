package me.jinjjahalgae.domain.participation.usecase;

import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.invite.util.InviteTestUtil;
import me.jinjjahalgae.domain.participation.entity.Participation;
import me.jinjjahalgae.domain.participation.enums.Role;
import me.jinjjahalgae.domain.user.User;
import me.jinjjahalgae.global.exception.AppException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class PatchSupervisorParticipationUseCaseImplTest {

    @Mock
    private ContractRepository contractRepository;

    @InjectMocks
    private PatchSupervisorParticipationUseCaseImpl patchSupervisorParticipationUseCase;

    private User supervisor;
    private Contract contract;
    private Participation participation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        supervisor = InviteTestUtil.createUser(1L, "supervisor", "supervisor_nick");
        contract = InviteTestUtil.createContract(1L, InviteTestUtil.createUser(2L, "contractor", "contractor_nick"));
        participation = InviteTestUtil.createParticipation(1L, contract, supervisor, Role.SUPERVISOR);

        // 테스트 데이터 설정
        ReflectionTestUtils.setField(contract, "status", ContractStatus.IN_PROGRESS);
        ReflectionTestUtils.setField(contract, "totalSupervisor", 1);
        contract.addParticipation(participation);
    }

    @Test
    @DisplayName("감독 중도 포기에 성공한다")
    void patchSupervisorParticipation_Success() {
        // given
        when(contractRepository.findById(contract.getId())).thenReturn(Optional.of(contract));

        // when
        patchSupervisorParticipationUseCase.execute(contract.getId(), supervisor);

        // then
        assertThat(participation.getValid()).isFalse(); // valid가 false로 변경되었는지 확인
        assertThat(contract.getTotalSupervisor()).isZero(); // 감독자 수가 감소했는지 확인
    }

    @Test
    @DisplayName("계약이 진행 중이 아니라서 중도 포기에 실패한다")
    void patchSupervisorParticipation_Fail_WhenNotInProgress() {
        // given
        ReflectionTestUtils.setField(contract, "status", ContractStatus.PENDING); // 상태 변경
        when(contractRepository.findById(contract.getId())).thenReturn(Optional.of(contract));

        // when & then
        assertThrows(AppException.class,
                () -> patchSupervisorParticipationUseCase.execute(contract.getId(), supervisor));
    }

    @Test
    @DisplayName("참여 정보가 없어 중도 포기에 실패한다")
    void patchSupervisorParticipation_Fail_WhenNotParticipated() {
        // given
        // 참여 정보를 제거
        contract.removeParticipation(participation);
        when(contractRepository.findById(contract.getId())).thenReturn(Optional.of(contract));

        // when & then
        assertThrows(AppException.class,
                () -> patchSupervisorParticipationUseCase.execute(contract.getId(), supervisor));
    }
}