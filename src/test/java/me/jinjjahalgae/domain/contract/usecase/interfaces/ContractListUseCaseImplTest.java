package me.jinjjahalgae.domain.contract.usecase.interfaces;

import me.jinjjahalgae.domain.contract.dto.response.ContractListResponse;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import me.jinjjahalgae.domain.contract.enums.ContractType;
import me.jinjjahalgae.domain.contract.mapper.ContractMapper;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ContractListUseCaseTest {

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private ContractMapper contractMapper;

    @InjectMocks
    private ContractListUseCaseImpl contractListUseCase;

    private User user;
    private Contract pendingContract;
    private Contract inProgressContract;
    private Contract completedContract;
    private Contract failedContract;
    private Contract abandonedContract;
    private Long userId;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        userId = 1L;
        pageable = PageRequest.of(0, 10);

        user = User.builder()
                .id(userId)
                .name("테스트유저")
                .build();

        // 테스트용 계약 생성
        pendingContract = createContract(1L, "운동하기", ContractStatus.PENDING);
        inProgressContract = createContract(2L, "독서하기", ContractStatus.IN_PROGRESS);
        completedContract = createContract(3L, "코딩하기", ContractStatus.COMPLETED);
        failedContract = createContract(4L, "다이어트하기", ContractStatus.FAILED);
        abandonedContract = createContract(5L, "영어공부하기", ContractStatus.ABANDONED);
    }

    @Test
    @DisplayName("메인페이지용 계약 목록 조회 성공 - PENDING, IN_PROGRESS")
    void getMainPageContracts_Success() {
        // Given
        List<ContractStatus> mainPageStatuses = List.of(ContractStatus.PENDING, ContractStatus.IN_PROGRESS);
        List<Contract> contracts = List.of(pendingContract, inProgressContract);
        Page<Contract> contractPage = new PageImpl<>(contracts, pageable, contracts.size());

        ContractListResponse pendingResponse = createContractListResponse(1L, "운동하기", ContractStatus.PENDING);
        ContractListResponse inProgressResponse = createContractListResponse(2L, "독서하기", ContractStatus.IN_PROGRESS);

        given(contractRepository.findByUserIdAndStatusInOrderByIdDesc(userId, mainPageStatuses, pageable))
                .willReturn(contractPage);
        given(contractMapper.toListResponse(pendingContract)).willReturn(pendingResponse);
        given(contractMapper.toListResponse(inProgressContract)).willReturn(inProgressResponse);

        // When
        Page<ContractListResponse> result = contractListUseCase.execute(userId, mainPageStatuses, pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).contractStatus()).isEqualTo(ContractStatus.PENDING);
        assertThat(result.getContent().get(1).contractStatus()).isEqualTo(ContractStatus.IN_PROGRESS);

        verify(contractRepository).findByUserIdAndStatusInOrderByIdDesc(userId, mainPageStatuses, pageable);
        verify(contractMapper).toListResponse(pendingContract);
        verify(contractMapper).toListResponse(inProgressContract);
    }

    @Test
    @DisplayName("히스토리용 계약 목록 조회 성공 - COMPLETED, FAILED, ABANDONED")
    void getHistoryContracts_Success() {
        // Given
        List<ContractStatus> historyStatuses = List.of(ContractStatus.COMPLETED, ContractStatus.FAILED, ContractStatus.ABANDONED);
        List<Contract> contracts = List.of(completedContract, failedContract, abandonedContract);
        Page<Contract> contractPage = new PageImpl<>(contracts, pageable, contracts.size());

        ContractListResponse completedResponse = createContractListResponse(3L, "코딩하기", ContractStatus.COMPLETED);
        ContractListResponse failedResponse = createContractListResponse(4L, "다이어트하기", ContractStatus.FAILED);
        ContractListResponse abandonedResponse = createContractListResponse(5L, "영어공부하기", ContractStatus.ABANDONED);

        given(contractRepository.findByUserIdAndStatusInOrderByIdDesc(userId, historyStatuses, pageable))
                .willReturn(contractPage);
        given(contractMapper.toListResponse(completedContract)).willReturn(completedResponse);
        given(contractMapper.toListResponse(failedContract)).willReturn(failedResponse);
        given(contractMapper.toListResponse(abandonedContract)).willReturn(abandonedResponse);

        // When
        Page<ContractListResponse> result = contractListUseCase.execute(userId, historyStatuses, pageable);

        // Then
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent().get(0).contractStatus()).isEqualTo(ContractStatus.COMPLETED);
        assertThat(result.getContent().get(1).contractStatus()).isEqualTo(ContractStatus.FAILED);
        assertThat(result.getContent().get(2).contractStatus()).isEqualTo(ContractStatus.ABANDONED);

        verify(contractRepository).findByUserIdAndStatusInOrderByIdDesc(userId, historyStatuses, pageable);
        verify(contractMapper).toListResponse(completedContract);
        verify(contractMapper).toListResponse(failedContract);
        verify(contractMapper).toListResponse(abandonedContract);
    }

    @Test
    @DisplayName("빈 목록 조회 성공")
    void getEmptyContractList_Success() {
        // Given
        List<ContractStatus> statuses = List.of(ContractStatus.PENDING);
        Page<Contract> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        given(contractRepository.findByUserIdAndStatusInOrderByIdDesc(userId, statuses, pageable))
                .willReturn(emptyPage);

        // When
        Page<ContractListResponse> result = contractListUseCase.execute(userId, statuses, pageable);

        // Then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);

        verify(contractRepository).findByUserIdAndStatusInOrderByIdDesc(userId, statuses, pageable);
    }

    @Test
    @DisplayName("특정 상태만 조회 성공")
    void getContractsBySpecificStatus_Success() {
        // Given
        List<ContractStatus> statuses = List.of(ContractStatus.COMPLETED);
        List<Contract> contracts = List.of(completedContract);
        Page<Contract> contractPage = new PageImpl<>(contracts, pageable, contracts.size());

        ContractListResponse completedResponse = createContractListResponse(3L, "코딩하기", ContractStatus.COMPLETED);

        given(contractRepository.findByUserIdAndStatusInOrderByIdDesc(userId, statuses, pageable))
                .willReturn(contractPage);
        given(contractMapper.toListResponse(completedContract)).willReturn(completedResponse);

        // When
        Page<ContractListResponse> result = contractListUseCase.execute(userId, statuses, pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).contractStatus()).isEqualTo(ContractStatus.COMPLETED);

        verify(contractRepository).findByUserIdAndStatusInOrderByIdDesc(userId, statuses, pageable);
        verify(contractMapper).toListResponse(completedContract);
    }

    private Contract createContract(Long id, String title, ContractStatus status) {
        Contract contract = Contract.builder()
                .user(user)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .title(title)
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
            idField.set(contract, id);

            java.lang.reflect.Field statusField = Contract.class.getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(contract, status);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return contract;
    }

    private ContractListResponse createContractListResponse(Long id, String title, ContractStatus status) {
        return new ContractListResponse(
                id,
                "uuid-" + id,
                title,
                status,
                3,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30),
                "보상",
                "벌칙",
                50.0,
                25.0
        );
    }
}