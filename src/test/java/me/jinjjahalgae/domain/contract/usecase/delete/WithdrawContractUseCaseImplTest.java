package me.jinjjahalgae.domain.contract.usecase.delete;

import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import me.jinjjahalgae.domain.contract.enums.ContractType;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.user.User;
import me.jinjjahalgae.global.exception.AppException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class WithdrawContractUseCaseImplTest {

    @Mock
    private ContractRepository contractRepository;

    @InjectMocks
    private WithdrawContractUseCaseImpl withdrawContractUseCase;

    @Test
    @DisplayName("계약 중도 포기 성공")
    void execute_Success() {
        // given
        Long userId = 1L;
        Long contractId = 1L;

        User user = createUser(userId);
        Contract contract = createContract(contractId, user, ContractStatus.IN_PROGRESS);

        given(contractRepository.findByIdWithUser(contractId)).willReturn(Optional.of(contract));

        // when
        withdrawContractUseCase.execute(userId, contractId);

        // then
        assertThat(contract.getStatus()).isEqualTo(ContractStatus.ABANDONED);
    }

    @Test
    @DisplayName("계약을 찾을 수 없음")
    void execute_ContractNotFound() {
        // given
        Long userId = 1L;
        Long contractId = 999L;

        given(contractRepository.findByIdWithUser(contractId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> withdrawContractUseCase.execute(userId, contractId))
                .isInstanceOf(AppException.class)
                .hasMessage("존재하지 않는 계약입니다.");
    }

    @Test
    @DisplayName("계약자가 아닌 경우 중도 포기 실패")
    void execute_NotContractor() {
        // given
        Long contractorId = 1L;
        Long otherUserId = 2L;
        Long contractId = 1L;

        User contractor = createUser(contractorId);
        Contract contract = createContract(contractId, contractor, ContractStatus.IN_PROGRESS);

        given(contractRepository.findByIdWithUser(contractId)).willReturn(Optional.of(contract));

        // when & then
        assertThatThrownBy(() -> withdrawContractUseCase.execute(otherUserId, contractId))
                .isInstanceOf(AppException.class)
                .hasMessage("계약에 대한 접근 권한이 없습니다.");
    }

    @Test
    @DisplayName("진행 중이 아닌 계약 중도 포기 실패")
    void execute_NotInProgress() {
        // given
        Long userId = 1L;
        Long contractId = 1L;

        User user = createUser(userId);
        Contract contract = createContract(contractId, user, ContractStatus.PENDING); // PENDING 상태

        given(contractRepository.findByIdWithUser(contractId)).willReturn(Optional.of(contract));

        // when & then
        assertThatThrownBy(() -> withdrawContractUseCase.execute(userId, contractId))
                .isInstanceOf(AppException.class)
                .hasMessage("진행 중인 계약만 포기할 수 있습니다.");
    }

    // Helper methods
    private User createUser(Long userId) {
        return User.builder()
                .id(userId)
                .email("test@example.com")
                .name("테스트 유저")
                .build();
    }

    private Contract createContract(Long contractId, User user,ContractStatus status) {
        Contract contract = Contract.builder()
                .user(user)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .title("테스트 계약")
                .goal("목표")
                .penalty("벌칙")
                .reward("보상")
                .life(3)
                .proofPerWeek(3)
                .oneOff(false)
                .type(ContractType.BASIC)
                .build();

        // 리플렉션으로 ID와 상태 설정 (실제 테스트에서는 ReflectionTestUtils 사용)
        try {
            java.lang.reflect.Field idField = Contract.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(contract, contractId);

            java.lang.reflect.Field statusField = Contract.class.getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(contract, status);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return contract;
    }
}