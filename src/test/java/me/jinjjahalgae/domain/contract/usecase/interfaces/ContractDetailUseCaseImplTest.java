package me.jinjjahalgae.domain.contract.usecase.interfaces;

import me.jinjjahalgae.domain.contract.usecase.get.detail.dto.ContractDetailResponse;
import me.jinjjahalgae.domain.contract.usecase.get.common.ContractBasicResponse;
import me.jinjjahalgae.domain.participation.usecase.common.ParticipantSimpleResponse;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import me.jinjjahalgae.domain.contract.enums.ContractType;
import me.jinjjahalgae.domain.contract.mapper.ContractMapper;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.contract.usecase.get.detail.GetContractDetailUseCaseImpl;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GetContractDetailUseCaseTest {

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private ContractMapper contractMapper;

    @InjectMocks
    private GetContractDetailUseCaseImpl contractDetailUseCase;

    private User contractor;
    private User supervisor1;
    private User supervisor2;
    private User supervisor3;
    private Contract contractWithParticipants;
    private ContractDetailResponse expectedResponse;
    private Long userId;
    private Long contractId;

    @BeforeEach
    void setUp() {
        userId = 1L;
        contractId = 1L;

        // 계약자 생성
        contractor = User.builder()
                .id(userId)
                .name("계약자")
                .build();

        // 감독자들 생성
        supervisor1 = User.builder()
                .id(2L)
                .name("감독자1")
                .build();

        supervisor2 = User.builder()
                .id(3L)
                .name("감독자2")
                .build();

        supervisor3 = User.builder()
                .id(4L)
                .name("감독자3")
                .build();

        // 계약 생성
        contractWithParticipants = Contract.builder()
                .user(contractor)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .title("운동하기")
                .goal("매일 30분 운동")
                .penalty("치킨 못 먹기")
                .reward("치킨 먹기")
                .life(3)
                .proofPerWeek(3)
                .oneOff(false)
                .type(ContractType.BASIC)
                .build();

        // 리플렉션으로 ID 설정
        try {
            java.lang.reflect.Field idField = Contract.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(contractWithParticipants, contractId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // ContractBasicResponse 생성
        ContractBasicResponse contractBasicResponse = new ContractBasicResponse(
                contractId,
                "uuid-123",
                "운동하기",
                "매일 30분 운동",
                3,
                "치킨 못 먹기",
                "치킨 먹기",
                12,
                3,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30)
        );

        // 참여자 정보 (계약자 1명 + 감독자 3명)
        expectedResponse = new ContractDetailResponse(
                contractBasicResponse,
                ContractStatus.PENDING,
                0,
                0,
                3,
                "0/12",
                "1/31",
                0.0,
                3.2,
                List.of(
                        new ParticipantSimpleResponse(
                                userId, "계약자", Role.CONTRACTOR, true
                        ),
                        new ParticipantSimpleResponse(
                                2L, "감독자1", Role.SUPERVISOR, true
                        ),
                        new ParticipantSimpleResponse(
                                3L, "감독자2", Role.SUPERVISOR, true
                        ),
                        new ParticipantSimpleResponse(
                                4L, "감독자3", Role.SUPERVISOR, true
                        )
                )
        );
    }

    @Test
    @DisplayName("계약 상세 조회 성공 - 참여자 4명 (계약자 1명 + 감독자 3명)")
    void getContractDetail_Success_WithMultipleParticipants() {
        // Given
        given(contractRepository.findDetailsByIdAndUserId(contractId, userId))
                .willReturn(Optional.of(contractWithParticipants));
        given(contractMapper.toDetailResponse(contractWithParticipants))
                .willReturn(expectedResponse);

        // When
        ContractDetailResponse result = contractDetailUseCase.execute(userId, contractId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.contractBasicResponse().contractId()).isEqualTo(contractId);
        assertThat(result.contractBasicResponse().title()).isEqualTo("운동하기");
        assertThat(result.contractBasicResponse().goal()).isEqualTo("매일 30분 운동");
        assertThat(result.contractBasicResponse().penalty()).isEqualTo("치킨 못 먹기");
        assertThat(result.contractBasicResponse().reward()).isEqualTo("치킨 먹기");
        assertThat(result.contractStatus()).isEqualTo(ContractStatus.PENDING);

        // 참여자 정보 검증
        assertThat(result.participants()).hasSize(4);

        // 계약자 검증
        ParticipantSimpleResponse contractor = result.participants().get(0);
        assertThat(contractor.userId()).isEqualTo(userId);
        assertThat(contractor.userName()).isEqualTo("계약자");
        assertThat(contractor.role()).isEqualTo(Role.CONTRACTOR);
        assertThat(contractor.valid()).isTrue();

        // 감독자들 검증
        ParticipantSimpleResponse supervisor1 = result.participants().get(1);
        assertThat(supervisor1.userId()).isEqualTo(2L);
        assertThat(supervisor1.userName()).isEqualTo("감독자1");
        assertThat(supervisor1.role()).isEqualTo(Role.SUPERVISOR);
        assertThat(supervisor1.valid()).isTrue();

        ParticipantSimpleResponse supervisor2 = result.participants().get(2);
        assertThat(supervisor2.userId()).isEqualTo(3L);
        assertThat(supervisor2.userName()).isEqualTo("감독자2");
        assertThat(supervisor2.role()).isEqualTo(Role.SUPERVISOR);
        assertThat(supervisor2.valid()).isTrue();

        ParticipantSimpleResponse supervisor3 = result.participants().get(3);
        assertThat(supervisor3.userId()).isEqualTo(4L);
        assertThat(supervisor3.userName()).isEqualTo("감독자3");
        assertThat(supervisor3.role()).isEqualTo(Role.SUPERVISOR);
        assertThat(supervisor3.valid()).isTrue();

        verify(contractRepository).findDetailsByIdAndUserId(contractId, userId);
        verify(contractMapper).toDetailResponse(contractWithParticipants);
    }

    @Test
    @DisplayName("중도포기한 감독자는 참여자 목록에서 제외됨")
    void getContractDetail_FilterInvalidSupervisor() {
        // Given
        User invalidSupervisor = User.builder()
                .id(5L)
                .name("중도포기 감독자")
                .build();

        // ContractBasicResponse 생성
        ContractBasicResponse contractBasicResponse = new ContractBasicResponse(
                contractId,
                "uuid-123",
                "운동하기",
                "매일 30분 운동",
                3,
                "치킨 못 먹기",
                "치킨 먹기",
                12,
                3,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30)
        );

        // valid=false인 감독자가 포함되지 않은 응답 (필터링된 결과)
        ContractDetailResponse responseWithValidParticipantsOnly = new ContractDetailResponse(
                contractBasicResponse,
                ContractStatus.PENDING,
                0,
                0,
                3,
                "0/12",
                "1/31",
                0.0,
                3.2,
                List.of(
                        new ParticipantSimpleResponse(
                                userId, "계약자", Role.CONTRACTOR, true
                        ),
                        new ParticipantSimpleResponse(
                                2L, "감독자1", Role.SUPERVISOR, true
                        ),
                        new ParticipantSimpleResponse(
                                3L, "감독자2", Role.SUPERVISOR, true
                        )
                )
        );

        given(contractRepository.findDetailsByIdAndUserId(contractId, userId))
                .willReturn(Optional.of(contractWithParticipants));
        given(contractMapper.toDetailResponse(contractWithParticipants))
                .willReturn(responseWithValidParticipantsOnly);

        // When
        ContractDetailResponse result = contractDetailUseCase.execute(userId, contractId);

        // Then
        assertThat(result.participants()).hasSize(3);
        assertThat(result.participants())
                .extracting(ParticipantSimpleResponse::userName)
                .containsExactly("계약자", "감독자1", "감독자2")
                .doesNotContain("중도포기 감독자");

        verify(contractRepository).findDetailsByIdAndUserId(contractId, userId);
        verify(contractMapper).toDetailResponse(contractWithParticipants);
    }

    @Test
    @DisplayName("감독자로 계약 상세 조회 성공")
    void getContractDetail_Success_AsSupervisor() {
        // Given
        Long supervisorId = 2L;

        given(contractRepository.findDetailsByIdAndUserId(contractId, supervisorId))
                .willReturn(Optional.of(contractWithParticipants));
        given(contractMapper.toDetailResponse(contractWithParticipants))
                .willReturn(expectedResponse);

        // When
        ContractDetailResponse result = contractDetailUseCase.execute(supervisorId, contractId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.participants()).hasSize(4);

        verify(contractRepository).findDetailsByIdAndUserId(contractId, supervisorId);
        verify(contractMapper).toDetailResponse(contractWithParticipants);
    }

    @Test
    @DisplayName("계약을 찾을 수 없을 때 예외 발생")
    void getContractDetail_NotFound() {
        // Given
        given(contractRepository.findDetailsByIdAndUserId(contractId, userId))
                .willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> contractDetailUseCase.execute(userId, contractId))
                .isInstanceOf(AppException.class)
                .hasMessage("존재하지 않는 계약입니다.");

        verify(contractRepository).findDetailsByIdAndUserId(contractId, userId);
    }

    @Test
    @DisplayName("다른 사용자의 계약 조회 시 예외 발생")
    void getContractDetail_Unauthorized() {
        // Given
        Long unauthorizedUserId = 999L;

        given(contractRepository.findDetailsByIdAndUserId(contractId, unauthorizedUserId))
                .willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> contractDetailUseCase.execute(unauthorizedUserId, contractId))
                .isInstanceOf(AppException.class)
                .hasMessage("존재하지 않는 계약입니다.");

        verify(contractRepository).findDetailsByIdAndUserId(contractId, unauthorizedUserId);
    }

    @Test
    @DisplayName("존재하지 않는 계약 ID로 조회 시 예외 발생")
    void getContractDetail_InvalidContractId() {
        // Given
        Long invalidContractId = 999L;

        given(contractRepository.findDetailsByIdAndUserId(invalidContractId, userId))
                .willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> contractDetailUseCase.execute(userId, invalidContractId))
                .isInstanceOf(AppException.class)
                .hasMessage("존재하지 않는 계약입니다.");

        verify(contractRepository).findDetailsByIdAndUserId(invalidContractId, userId);
    }
}