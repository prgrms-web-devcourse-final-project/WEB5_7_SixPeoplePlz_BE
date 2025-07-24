package me.jinjjahalgae.domain.contract.usecase.interfaces;

import jakarta.persistence.EntityManager;
import me.jinjjahalgae.domain.contract.usecase.update.dto.ContractUpdateRequest;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractType;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.contract.usecase.update.UpdateContractUseCaseImpl;
import me.jinjjahalgae.domain.participation.entity.Participation;
import me.jinjjahalgae.domain.participation.enums.Role;
import me.jinjjahalgae.domain.participation.mapper.ParticipationMapper;
import me.jinjjahalgae.domain.user.User;
import me.jinjjahalgae.domain.user.UserRepository;
import me.jinjjahalgae.global.exception.AppException;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateContractUseCaseTest {

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private ParticipationMapper participationMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UpdateContractUseCaseImpl contractUpdateUseCase;

    private User contractor;
    private User otherUser;
    private User supervisor;
    private Contract contract;
    private ContractUpdateRequest updateRequest;
    private Participation newParticipation;
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
                .startDate(Instant.from(LocalDateTime.now().plusDays(1)))
                .endDate(Instant.from(LocalDateTime.now().plusDays(31)))
                .title("운동하기")
                .goal("매일 30분 운동")
                .penalty("치킨 못 먹기")
                .reward("치킨 먹기")
                .totalProof(10)
                .oneOff(false)
                .type(ContractType.BASIC)
                .build();

        contract.initialize();
        ReflectionTestUtils.setField(contract, "id", contractId);

        newParticipation = Participation.builder()
                .contract(contract)
                .user(contractor)
                .imageKey("signature/new_signature_123.png")
                .role(Role.CONTRACTOR)
                .valid(true)
                .build();

        // 업데이트 요청 생성 (이미 signatureImageKey 포함되어 있음)
        updateRequest = new ContractUpdateRequest(
                "수정된 운동하기",
                "매일 1시간 운동",
                "치킨 2번 못 먹기",
                "치킨 2번 먹기",
                15,
                false,
                LocalDateTime.now().plusDays(2).toInstant(ZoneOffset.UTC),
                LocalDateTime.now().plusDays(32).toInstant(ZoneOffset.UTC),
                "BASIC",
                "signature/updated_signature_123.png"
        );
    }

    @Test
    @DisplayName("계약 수정 성공 - 감독자가 없는 경우")
    void update_Success() {
        // Given
        given(contractRepository.findByIdWithUser(contractId))
                .willReturn(Optional.of(contract));
        given(userRepository.findByIdAndDeletedAtIsNull(contractorId))
                .willReturn(Optional.of(contractor));
        given(participationMapper.toEntity(any(), any(), anyString(), eq(Role.CONTRACTOR), eq(true)))
                .willReturn(newParticipation);

        // When
        contractUpdateUseCase.execute(contractorId, contractId, updateRequest);

        // Then
        verify(contractRepository).findByIdWithUser(contractId);
        verify(userRepository).findByIdAndDeletedAtIsNull(contractorId);
        verify(participationMapper).toEntity(contract, contractor, "signature/updated_signature_123.png", Role.CONTRACTOR, true);

        // 계약 내용이 업데이트되었는지 확인
        assertThat(contract.getTitle()).isEqualTo("수정된 운동하기");
        assertThat(contract.getGoal()).isEqualTo("매일 1시간 운동");
        assertThat(contract.getPenalty()).isEqualTo("치킨 2번 못 먹기");
        assertThat(contract.getReward()).isEqualTo("치킨 2번 먹기");
        assertThat(contract.getTotalProof()).isEqualTo(15);
        assertThat(contract.getType()).isEqualTo(ContractType.BASIC);
    }

    @Test
    @DisplayName("계약을 찾을 수 없는 경우 예외 발생")
    void updateContract_NotFound() {
        // Given
        given(contractRepository.findByIdWithUser(contractId))
                .willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> contractUpdateUseCase.execute(contractorId, contractId, updateRequest))
                .isInstanceOf(AppException.class)
                .hasMessage("존재하지 않는 계약입니다.");

        verify(contractRepository).findByIdWithUser(contractId);
    }

    @Test
    @DisplayName("접근 권한이 없는 경우 예외 발생")
    void update_AccessDenied() {
        // Given
        Long otherUserId = 999L;
        given(contractRepository.findByIdWithUser(contractId))
                .willReturn(Optional.of(contract));

        // When & Then
        assertThatThrownBy(() -> contractUpdateUseCase.execute(otherUserId, contractId, updateRequest))
                .isInstanceOf(AppException.class)
                .hasMessage("계약에 대한 접근 권한이 없습니다.");

        verify(contractRepository).findByIdWithUser(contractId);
    }

    @Test
    @DisplayName("감독자가 서명한 계약 수정 시 예외 발생")
    void update_SupervisorAlreadySigned() {
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

        given(contractRepository.findByIdWithUser(contractId))
                .willReturn(Optional.of(contract));

        // When & Then
        assertThatThrownBy(() -> contractUpdateUseCase.execute(contractorId, contractId, updateRequest))
                .isInstanceOf(AppException.class)
                .hasMessage("감독자가 서명한 계약은 수정할 수 없습니다.");

        verify(contractRepository).findByIdWithUser(contractId);
    }

    @Test
    @DisplayName("계약 수정 후 totalProof가 재계산됨")
    void update_RecalculatesTotalProof() {
        // Given
        given(contractRepository.findByIdWithUser(contractId))
                .willReturn(Optional.of(contract));
        given(userRepository.findByIdAndDeletedAtIsNull(contractorId))
                .willReturn(Optional.of(contractor));
        given(participationMapper.toEntity(any(), any(), anyString(), eq(Role.CONTRACTOR), eq(true)))
                .willReturn(newParticipation);

        int originalTotalProof = contract.getTotalProof();

        // 기간을 더 길게 하고 주간 인증 횟수를 늘린 요청
        ContractUpdateRequest longerRequest = new ContractUpdateRequest(
                "수정된 운동하기",
                "매일 1시간 운동",
                "치킨 2번 못 먹기",
                "치킨 2번 먹기",
                25,
                false,
                LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC),
                LocalDateTime.now().plusDays(60).toInstant(ZoneOffset.UTC),
                "BASIC",
                "signature/updated_signature_123.png"
        );

        // When
        contractUpdateUseCase.execute(contractorId, contractId, longerRequest);

        // Then
        verify(contractRepository).findByIdWithUser(contractId);
        verify(userRepository).findByIdAndDeletedAtIsNull(contractorId);

        // totalProof가 변경되었는지 확인
        assertThat(contract.getTotalProof()).isNotEqualTo(originalTotalProof);
        assertThat(contract.getTotalProof()).isEqualTo(25);
    }

    @Test
    @DisplayName("계약자만 본인의 계약을 수정할 수 있음")
    void updateContract_OnlyContractorCanUpdate() {
        // Given
        given(contractRepository.findByIdWithUser(contractId))
                .willReturn(Optional.of(contract));
        given(userRepository.findByIdAndDeletedAtIsNull(contractorId))
                .willReturn(Optional.of(contractor));
        given(participationMapper.toEntity(any(), any(), anyString(), eq(Role.CONTRACTOR), eq(true)))
                .willReturn(newParticipation);

        // When - 계약자가 본인 계약 수정
        contractUpdateUseCase.execute(contractorId, contractId, updateRequest);

        // Then
        verify(contractRepository).findByIdWithUser(contractId);
        assertThat(contract.getTitle()).isEqualTo("수정된 운동하기");

        // When & Then - 다른 사용자가 수정 시도하면 예외 발생
        assertThatThrownBy(() -> contractUpdateUseCase.execute(999L, contractId, updateRequest))
                .isInstanceOf(AppException.class)
                .hasMessage("계약에 대한 접근 권한이 없습니다.");
    }

    @Test
    @DisplayName("계약 수정 시 기존 서명 삭제되고 새 서명 추가")
    void update_Success_WithResignature() {
        // Given
        int initialParticipationCount = contract.getParticipations().size();

        given(contractRepository.findByIdWithUser(contractId))
                .willReturn(Optional.of(contract));
        given(userRepository.findByIdAndDeletedAtIsNull(contractorId))
                .willReturn(Optional.of(contractor));

        Participation newSignature = Participation.builder()
                .contract(contract)
                .user(contractor)
                .imageKey("signature/new_signature_123.png")
                .role(Role.CONTRACTOR)
                .valid(true)
                .build();

        given(participationMapper.toEntity(any(), any(), anyString(), eq(Role.CONTRACTOR), eq(true)))
                .willReturn(newSignature);

        // When
        contractUpdateUseCase.execute(contractorId, contractId, updateRequest);

        // Then
        verify(contractRepository).findByIdWithUser(contractId);
        verify(userRepository).findByIdAndDeletedAtIsNull(contractorId);
        verify(participationMapper).toEntity(contract, contractor, "signature/updated_signature_123.png", Role.CONTRACTOR, true);

        // 계약 내용 수정 확인
        assertThat(contract.getTitle()).isEqualTo("수정된 운동하기");

        // 참여자 수 확인 (기존 계약자 삭제 + 새 계약자 추가 = 동일)
        assertThat(contract.getParticipations()).hasSize(initialParticipationCount);

        // 새로운 계약자 서명 확인
        boolean hasNewContractorSignature = contract.getParticipations().stream()
                .anyMatch(p -> p.getRole() == Role.CONTRACTOR &&
                        p.getImageKey().equals("signature/new_signature_123.png"));
        assertThat(hasNewContractorSignature).isTrue();
    }

    @Test
    @DisplayName("계약 수정 후 계약자는 정확히 1명만 존재")
    void update_OnlyOneContractorExists() {
        // Given
        given(contractRepository.findByIdWithUser(contractId))
                .willReturn(Optional.of(contract));
        given(userRepository.findByIdAndDeletedAtIsNull(contractorId))
                .willReturn(Optional.of(contractor));
        given(participationMapper.toEntity(any(), any(), anyString(), eq(Role.CONTRACTOR), eq(true)))
                .willReturn(newParticipation);

        // When
        contractUpdateUseCase.execute(contractorId, contractId, updateRequest);

        // Then
        long contractorCount = contract.getParticipations().stream()
                .filter(p -> p.getRole() == Role.CONTRACTOR)
                .count();

        assertThat(contractorCount).isEqualTo(1);
    }

    @Test
    @DisplayName("낙관적 락 충돌 시 예외 발생")
    void update_OptimisticLockingFailure() {
        // Given
        given(contractRepository.findByIdWithUser(contractId))
                .willReturn(Optional.of(contract));
        given(userRepository.findByIdAndDeletedAtIsNull(contractorId))
                .willReturn(Optional.of(contractor));
        given(participationMapper.toEntity(any(), any(), anyString(), eq(Role.CONTRACTOR), eq(true)))
                .willReturn(newParticipation);

        // EntityManager.flush()에서 OptimisticLockingFailureException 발생
        doThrow(new OptimisticLockingFailureException("Optimistic locking failure"))
                .when(entityManager).flush();

        // When & Then
        assertThatThrownBy(() -> contractUpdateUseCase.execute(contractorId, contractId, updateRequest))
                .isInstanceOf(AppException.class)
                .hasMessage("계약 상태 변경 중 충돌이 발생했습니다. 다시 시도해주세요.");
    }
}