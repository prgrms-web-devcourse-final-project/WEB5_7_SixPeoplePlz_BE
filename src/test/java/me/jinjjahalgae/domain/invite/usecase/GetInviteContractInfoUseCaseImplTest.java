package me.jinjjahalgae.domain.invite.usecase;

import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.invite.usecase.get.contract.dto.InviteContractInfoResponse;
import me.jinjjahalgae.domain.invite.usecase.get.contract.GetInviteContractInfoUseCaseImpl;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class GetInviteContractInfoUseCaseImplTest {

    @Mock
    private ContractRepository contractRepository;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ValueOperations<String, Object> valueOperations;
    @InjectMocks
    private GetInviteContractInfoUseCaseImpl getInviteContractInfoUseCase;

    private User contractor;
    private User newSupervisor;
    private Contract contract;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        ReflectionTestUtils.setField(getInviteContractInfoUseCase, "SUPERVISOR_COUNT_PREFIX", "contract:supervisors:");

        // 공통 테스트 데이터 설정
        contractor = InviteTestUtil.createUser(1L, "contractor", "contractor_nick");
        newSupervisor = InviteTestUtil.createUser(2L, "newSupervisor", "newSupervisor_nick");
        contract = InviteTestUtil.createContract(1L, contractor);
        Participation contractorParticipation = InviteTestUtil.createParticipation(1L, contract, contractor, Role.CONTRACTOR);
        contract.addParticipation(contractorParticipation);
    }

    @Test
    @DisplayName("초대 계약서 정보를 정상적으로 조회한다")
    void getInviteContractInfo_Success() {
        // given
        String contractUuid = contract.getUuid();
        String supervisorCountKey = "contract:supervisors:" + contract.getId();

        when(contractRepository.findByUuid(contractUuid)).thenReturn(Optional.of(contract)); // N+1 해결을 위해 findByUuidWithParticipations 사용 권장
        when(valueOperations.get(supervisorCountKey)).thenReturn(5); // 자리가 남아있음

        // when
        InviteContractInfoResponse result = getInviteContractInfoUseCase.execute(contractUuid, newSupervisor);

        // then
        assertThat(result).isNotNull();
        assertThat(result.uuid()).isEqualTo(contractUuid);
        assertThat(result.contractorName()).isEqualTo(contractor.getName());
    }

    @Test
    @DisplayName("감독자가 모두 찼을 경우 예외를 발생시킨다")
    void getInviteContractInfo_WhenFull_ThrowsException() {
        // given
        String contractUuid = contract.getUuid();
        String supervisorCountKey = "contract:supervisors:" + contract.getId();

        when(contractRepository.findByUuid(contractUuid)).thenReturn(Optional.of(contract));
        when(valueOperations.get(supervisorCountKey)).thenReturn(0); // 자리가 없음

        // when & then
        assertThrows(AppException.class, () -> getInviteContractInfoUseCase.execute(contractUuid, newSupervisor));
    }

    @Test
    @DisplayName("이미 참여한 사용자가 접근 시 예외를 발생시킨다")
    void getInviteContractInfo_WhenAlreadyParticipated_ThrowsException() {
        // given
        String contractUuid = contract.getUuid();
        // 접근하려는 사용자가 이미 계약자로 참여한 사용자
        User userWhoAlreadyParticipated = contractor;

        when(contractRepository.findByUuid(contractUuid)).thenReturn(Optional.of(contract));
        when(valueOperations.get(anyString())).thenReturn(5); // 자리는 남아있음

        // when & then
        assertThrows(AppException.class, () -> getInviteContractInfoUseCase.execute(contractUuid, userWhoAlreadyParticipated));
    }
}