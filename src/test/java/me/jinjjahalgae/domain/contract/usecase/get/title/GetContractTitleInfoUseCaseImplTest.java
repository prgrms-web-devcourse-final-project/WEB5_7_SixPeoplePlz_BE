package me.jinjjahalgae.domain.contract.usecase.get.title;

import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractType;
import me.jinjjahalgae.domain.contract.mapper.ContractMapper;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.contract.usecase.get.title.dto.ContractTitleInfoResponse;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ContractTitleInfoUseCaseTest {

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private ContractMapper contractMapper;

    @InjectMocks
    private GetContractTitleInfoUseCaseImpl contractTitleInfoUseCase;

    private User user;
    private Contract contract;
    private ContractTitleInfoResponse expectedResponse;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("테스트 사용자")
                .build();

        contract = Contract.builder()
                .user(user)
                .title("매일 운동하기")
                .goal("매일 30분 이상 운동하기")
                .penalty("치킨 못 먹기")
                .reward("치킨 먹기")
                .life(3)
                .proofPerWeek(7)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .type(ContractType.BASIC)
                .oneOff(false)
                .build();

        expectedResponse = new ContractTitleInfoResponse("매일 운동하기", "매일 30분 이상 운동하기");
    }

    @Test
    @DisplayName("계약 제목 정보 조회 성공")
    void getContractTitleInfo_Success() {
        // Given
        Long userId = 1L;
        Long contractId = 1L;

        given(contractRepository.findValidParticipantByIdAndUserId(contractId, userId))
                .willReturn(Optional.of(contract));
        given(contractMapper.toTitleInfoResponse(contract))
                .willReturn(expectedResponse);

        // When
        ContractTitleInfoResponse result = contractTitleInfoUseCase.execute(userId, contractId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("매일 운동하기");
        assertThat(result.goal()).isEqualTo("매일 30분 이상 운동하기");

        verify(contractRepository).findValidParticipantByIdAndUserId(contractId, userId);
        verify(contractMapper).toTitleInfoResponse(contract);
    }

    @Test
    @DisplayName("계약 제목 정보 조회 실패 - 계약을 찾을 수 없음")
    void getContractTitleInfo_Fail_ContractNotFound() {
        // Given
        Long userId = 1L;
        Long contractId = 999L;

        given(contractRepository.findValidParticipantByIdAndUserId(contractId, userId))
                .willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> contractTitleInfoUseCase.execute(userId, contractId))
                .isInstanceOf(AppException.class)
                .hasMessage("계약에 대한 접근 권한이 없습니다.");

        verify(contractRepository).findValidParticipantByIdAndUserId(contractId, userId);
    }

    @Test
    @DisplayName("계약 제목 정보 조회 실패 - 유효하지 않은 참여자")
    void getContractTitleInfo_Fail_InvalidParticipant() {
        // Given
        Long userId = 999L;
        Long contractId = 1L;

        given(contractRepository.findValidParticipantByIdAndUserId(contractId, userId))
                .willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> contractTitleInfoUseCase.execute(userId, contractId))
                .isInstanceOf(AppException.class)
                .hasMessage("계약에 대한 접근 권한이 없습니다.");

        verify(contractRepository).findValidParticipantByIdAndUserId(contractId, userId);
    }
}