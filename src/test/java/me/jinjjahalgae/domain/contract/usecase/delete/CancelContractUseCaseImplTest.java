package me.jinjjahalgae.domain.contract.usecase.delete;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.util.Optional;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.user.User;
import me.jinjjahalgae.global.exception.AppException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CancelContractUseCaseImplTest {

    @Mock
    private ContractRepository contractRepository;

    @InjectMocks
    private CancelContractUseCaseImpl cancelContractUseCase;

    @Test
    @DisplayName("계약 시작 전 취소")
    void cancel_Success() {
        // given
        Long userId = 1L;
        Long contractId = 1L;
        User user = createUser(userId);
        // "PENDING" 상태의 계약을 준비
        Contract contract = createContract(contractId, user, ContractStatus.PENDING);

        given(contractRepository.findById(contractId)).willReturn(Optional.of(contract));

        // when
        cancelContractUseCase.execute(userId, contractId);

        // then
        // cancelContract()는 상태 변경이 아닌 삭제이므로, delete 메서드가 호출되었는지만 검증하면 된다.
        verify(contractRepository, times(1)).delete(contract);
    }

    @Test
    @DisplayName("존재하지 않는 계약을 취소하려 할 때")
    void cancelContract_NotFound() {
        // given
        Long userId = 1L;
        Long contractId = 999L;
        given(contractRepository.findById(contractId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> cancelContractUseCase.execute(userId, contractId))
                .isInstanceOf(AppException.class)
                .hasMessage("존재하지 않는 계약입니다.");
    }

    @Test
    @DisplayName("계약자가 아닐 때")
    void cancel_AccessDenied() {
        // given
        Long contractorId = 1L;
        Long otherUserId = 2L;
        Long contractId = 1L;
        User contractor = createUser(contractorId);
        Contract contract = createContract(contractId, contractor, ContractStatus.PENDING);

        given(contractRepository.findById(contractId)).willReturn(Optional.of(contract));

        // when & then
        assertThatThrownBy(() -> cancelContractUseCase.execute(otherUserId, contractId))
                .isInstanceOf(AppException.class)
                .hasMessage("계약에 대한 접근 권한이 없습니다.");
    }

    @Test
    @DisplayName("이미 진행 중인 계약을 취소하려 할 때")
    void cancel_AlreadyInProgress() {
        // given
        Long userId = 1L;
        Long contractId = 1L;
        User user = createUser(userId);
        // "IN_PROGRESS" 상태의 계약을 준비
        Contract contract = createContract(contractId, user, ContractStatus.IN_PROGRESS);

        given(contractRepository.findById(contractId)).willReturn(Optional.of(contract));

        // when & then
        assertThatThrownBy(() -> cancelContractUseCase.execute(userId, contractId))
                .isInstanceOf(AppException.class)
                .hasMessage("시작 전인 계약만 포기할 수 있습니다.");
    }

    private User createUser(Long userId) {
        return User.builder().id(userId).name("테스트유저").build();
    }

    private Contract createContract(Long contractId, User user, ContractStatus status) {
        Contract contract = Contract.builder().user(user).build();
        ReflectionTestUtils.setField(contract, "id", contractId);
        ReflectionTestUtils.setField(contract, "status", status);
        return contract;
    }
}