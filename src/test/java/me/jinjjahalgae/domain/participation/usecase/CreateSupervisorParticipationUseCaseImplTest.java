package me.jinjjahalgae.domain.participation.usecase;

import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.invite.util.InviteTestUtil;
import me.jinjjahalgae.domain.participation.usecase.create.contractor.dto.CreateContractorParticipationRequest;
import me.jinjjahalgae.domain.participation.usecase.create.supervisor.CreateSupervisorParticipationUseCaseImpl;
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

class CreateSupervisorParticipationUseCaseImplTest {

    @Mock
    private ContractRepository contractRepository;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private CreateSupervisorParticipationUseCaseImpl createSupervisorParticipationUseCase;

    private User user;
    private Contract contract;
    private String supervisorCountKey;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        ReflectionTestUtils.setField(createSupervisorParticipationUseCase, "SUPERVISOR_COUNT_PREFIX", "contract:supervisors:");

        user = InviteTestUtil.createUser(1L, "supervisor", "supervisor_nick");
        contract = InviteTestUtil.createContract(1L, InviteTestUtil.createUser(2L, "contractor", "contractor_nick"));
        supervisorCountKey = "contract:supervisors:" + contract.getId();
    }

    @Test
    @DisplayName("감독자 참여에 성공한다")
    void createSupervisorParticipation_Success() {
        // given
        CreateContractorParticipationRequest request = new CreateContractorParticipationRequest("image-key");
        when(contractRepository.findById(contract.getId())).thenReturn(Optional.of(contract));
        when(valueOperations.get(supervisorCountKey)).thenReturn(5); // 자리가 남아있음

        // when
        createSupervisorParticipationUseCase.execute(contract.getId(), request, user);

        // then
        assertThat(contract.getParticipations()).hasSize(1);
        assertThat(contract.getParticipations().get(0).getUser()).isEqualTo(user);
        verify(valueOperations, times(1)).decrement(supervisorCountKey);
    }

    @Test
    @DisplayName("감독자 자리가 없어 참여에 실패한다")
    void createSupervisorParticipation_Fail_WhenFull() {
        // given
        CreateContractorParticipationRequest request = new CreateContractorParticipationRequest("image-key");
        when(contractRepository.findById(contract.getId())).thenReturn(Optional.of(contract));
        when(valueOperations.get(supervisorCountKey)).thenReturn(0); // 자리가 없음

        // when & then
        assertThrows(AppException.class,
                () -> createSupervisorParticipationUseCase.execute(contract.getId(), request, user));
    }

    @Test
    @DisplayName("이미 참여한 계약이라 참여에 실패한다")
    void createSupervisorParticipation_Fail_WhenAlreadyParticipated() {
        // given
        CreateContractorParticipationRequest request = new CreateContractorParticipationRequest("image-key");
        // 이미 참여한 상태로 설정
        contract.addParticipation(InviteTestUtil.createParticipation(1L, contract, user, null));

        when(contractRepository.findById(contract.getId())).thenReturn(Optional.of(contract));
        when(valueOperations.get(supervisorCountKey)).thenReturn(5);

        // when & then
        assertThrows(AppException.class,
                () -> createSupervisorParticipationUseCase.execute(contract.getId(), request, user));
    }
}