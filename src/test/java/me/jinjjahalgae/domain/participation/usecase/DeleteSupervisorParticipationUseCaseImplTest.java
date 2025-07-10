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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class DeleteSupervisorParticipationUseCaseImplTest {

    @Mock
    private ContractRepository contractRepository;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private DeleteSupervisorParticipationUseCaseImpl deleteSupervisorParticipationUseCase;

    private User supervisor;
    private Contract contract;
    private String supervisorCountKey;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        ReflectionTestUtils.setField(deleteSupervisorParticipationUseCase, "SUPERVISOR_COUNT_PREFIX", "contract:supervisors:");

        supervisor = InviteTestUtil.createUser(1L, "supervisor", "supervisor_nick");
        contract = InviteTestUtil.createContract(1L, InviteTestUtil.createUser(2L, "contractor", "contractor_nick"));
        supervisorCountKey = "contract:supervisors:" + contract.getId();
    }

    @Test
    @DisplayName("감독 철회에 성공한다")
    void deleteSupervisorParticipation_Success() {
        // given
        Participation participation = InviteTestUtil.createParticipation(1L, contract, supervisor, Role.SUPERVISOR);
        contract.addParticipation(participation);
        assertThat(contract.getParticipations()).hasSize(1); // 초기 상태 확인

        when(contractRepository.findById(contract.getId())).thenReturn(Optional.of(contract));

        // when
        deleteSupervisorParticipationUseCase.execute(contract.getId(), supervisor);

        // then
        assertThat(contract.getParticipations()).isEmpty(); // 참여 정보가 삭제되었는지 확인
        verify(valueOperations, times(1)).increment(supervisorCountKey);
    }

    @Test
    @DisplayName("계약이 시작되어 감독 철회에 실패한다")
    void deleteSupervisorParticipation_Fail_WhenContractStarted() {
        // given
        ReflectionTestUtils.setField(contract, "status", ContractStatus.IN_PROGRESS); // 계약 상태 변경
        when(contractRepository.findById(contract.getId())).thenReturn(Optional.of(contract));

        // when & then
        assertThrows(AppException.class,
                () -> deleteSupervisorParticipationUseCase.execute(contract.getId(), supervisor));
    }

    @Test
    @DisplayName("참여 정보가 없어 감독 철회에 실패한다")
    void deleteSupervisorParticipation_Fail_WhenNotParticipated() {
        // given
        // 참여 정보를 추가하지 않음
        when(contractRepository.findById(contract.getId())).thenReturn(Optional.of(contract));

        // when & then
        assertThrows(AppException.class,
                () -> deleteSupervisorParticipationUseCase.execute(contract.getId(), supervisor));
    }
}