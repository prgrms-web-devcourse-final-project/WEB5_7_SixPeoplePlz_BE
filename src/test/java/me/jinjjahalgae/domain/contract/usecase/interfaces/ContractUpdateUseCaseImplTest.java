package me.jinjjahalgae.domain.contract.usecase.interfaces;

import me.jinjjahalgae.domain.contract.usecase.update.ContractUpdateRequest;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractType;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.contract.usecase.update.UpdateContractUpdateUseCaseImpl;
import me.jinjjahalgae.domain.participation.entity.Participation;
import me.jinjjahalgae.domain.participation.enums.Role;
import me.jinjjahalgae.domain.user.User;
import me.jinjjahalgae.global.exception.AppException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateContractUpdateUseCaseTest {

    @Mock
    private ContractRepository contractRepository;

    @InjectMocks
    private UpdateContractUpdateUseCaseImpl contractUpdateUseCase;

    private User contractor;
    private User otherUser;
    private User supervisor;
    private Contract contract;
    private ContractUpdateRequest updateRequest;
    private Long contractorId;
    private Long contractId;

    @BeforeEach
    void setUp() {
        contractorId = 1L;
        contractId = 1L;

        // 계약자 생성
        contractor = User.builder()
                .id(contractorId)
                .name("계약자")
                .email("contractor@test.com")
                .nickname("계약자닉네임")
                .build();

        // 다른 사용자 생성
        otherUser = User.builder()
                .id(999L)
                .name("다른사용자")
                .email("other@test.com")
                .nickname("다른사용자닉네임")
                .build();

        // 감독자 생성
        supervisor = User.builder()
                .id(2L)
                .name("감독자")
                .email("supervisor@test.com")
                .nickname("감독자닉네임")
                .build();

        // 계약 생성
        contract = Contract.builder()
                .user(contractor)
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(31))
                .title("운동하기")
                .goal("매일 30분 운동")
                .penalty("치킨 못 먹기")
                .reward("치킨 먹기")
                .life(3)
                .proofPerWeek(3)
                .oneOff(false)
                .type(ContractType.BASIC)
                .build();

        contract.initialize();
        ReflectionTestUtils.setField(contract, "id", contractId);

        // 업데이트 요청 생성
        updateRequest = new ContractUpdateRequest(
                "수정된 운동하기",
                "매일 1시간 운동",
                "치킨 2번 못 먹기",
                "치킨 2번 먹기",
                5,
                5,
                false,
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(32),
                "BASIC"
        );
    }

    @Test
    @DisplayName("계약 수정 성공 - 감독자가 없는 경우")
    void updateContract_Success() {
        // Given
        given(contractRepository.findById(contractId))
                .willReturn(Optional.of(contract));

        // When
        contractUpdateUseCase.execute(contractorId, contractId, updateRequest);

        // Then
        verify(contractRepository).findById(contractId);

        // 계약 내용이 업데이트되었는지 확인
        assertThat(contract.getTitle()).isEqualTo("수정된 운동하기");
        assertThat(contract.getGoal()).isEqualTo("매일 1시간 운동");
        assertThat(contract.getPenalty()).isEqualTo("치킨 2번 못 먹기");
        assertThat(contract.getReward()).isEqualTo("치킨 2번 먹기");
        assertThat(contract.getLife()).isEqualTo(5);
        assertThat(contract.getProofPerWeek()).isEqualTo(5);
        assertThat(contract.getType()).isEqualTo(ContractType.BASIC);
    }

    @Test
    @DisplayName("계약을 찾을 수 없는 경우 예외 발생")
    void updateContract_ContractNotFound() {
        // Given
        given(contractRepository.findById(contractId))
                .willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> contractUpdateUseCase.execute(contractorId, contractId, updateRequest))
                .isInstanceOf(AppException.class)
                .hasMessage("존재하지 않는 계약입니다.");

        verify(contractRepository).findById(contractId);
    }

    @Test
    @DisplayName("접근 권한이 없는 경우 예외 발생")
    void updateContract_AccessDenied() {
        // Given
        Long otherUserId = 999L;
        given(contractRepository.findById(contractId))
                .willReturn(Optional.of(contract));

        // When & Then
        assertThatThrownBy(() -> contractUpdateUseCase.execute(otherUserId, contractId, updateRequest))
                .isInstanceOf(AppException.class)
                .hasMessage("계약에 대한 접근 권한이 없습니다.");

        verify(contractRepository).findById(contractId);
    }

    @Test
    @DisplayName("감독자가 서명한 계약 수정 시 예외 발생")
    void updateContract_SupervisorAlreadySigned() {
        // Given
        // 감독자 참여 정보 추가
        Participation supervisorParticipation = Participation.builder()
                .contract(contract)
                .user(supervisor)
                .role(Role.SUPERVISOR)
                .imageKey("supervisor-signature")
                .valid(true)
                .build();

        contract.addParticipation(supervisorParticipation);

        given(contractRepository.findById(contractId))
                .willReturn(Optional.of(contract));

        // When & Then
        assertThatThrownBy(() -> contractUpdateUseCase.execute(contractorId, contractId, updateRequest))
                .isInstanceOf(AppException.class)
                .hasMessage("감독자가 서명한 계약은 수정할 수 없습니다.");

        verify(contractRepository).findById(contractId);
    }

    @Test
    @DisplayName("계약 수정 후 totalProof가 재계산됨")
    void updateContract_RecalculatesTotalProof() {
        // Given
        given(contractRepository.findById(contractId))
                .willReturn(Optional.of(contract));

        int originalTotalProof = contract.getTotalProof();

        // 기간을 더 길게 하고 주간 인증 횟수를 늘린 요청
        ContractUpdateRequest longerRequest = new ContractUpdateRequest(
                "수정된 운동하기",
                "매일 1시간 운동",
                "치킨 2번 못 먹기",
                "치킨 2번 먹기",
                5,
                7, // 주간 인증 횟수 증가
                false,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(60), // 기간 연장
                "BASIC"
        );

        // When
        contractUpdateUseCase.execute(contractorId, contractId, longerRequest);

        // Then
        verify(contractRepository).findById(contractId);

        // totalProof가 재계산되어 달라졌는지 확인
        assertThat(contract.getTotalProof()).isNotEqualTo(originalTotalProof);
        assertThat(contract.getTotalProof()).isGreaterThan(originalTotalProof);
    }

    @Test
    @DisplayName("계약자만 본인의 계약을 수정할 수 있음")
    void updateContract_OnlyContractorCanUpdate() {
        // Given
        given(contractRepository.findById(contractId))
                .willReturn(Optional.of(contract));

        // When - 계약자가 본인 계약 수정
        contractUpdateUseCase.execute(contractorId, contractId, updateRequest);

        // Then
        verify(contractRepository).findById(contractId);
        assertThat(contract.getTitle()).isEqualTo("수정된 운동하기");

        // When & Then - 다른 사용자가 수정 시도하면 예외 발생
        assertThatThrownBy(() -> contractUpdateUseCase.execute(999L, contractId, updateRequest))
                .isInstanceOf(AppException.class)
                .hasMessage("계약에 대한 접근 권한이 없습니다.");
    }
}