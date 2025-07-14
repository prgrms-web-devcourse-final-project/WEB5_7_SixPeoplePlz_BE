package me.jinjjahalgae.domain.contract.usecase.interfaces;

import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import me.jinjjahalgae.domain.contract.enums.ContractType;
import me.jinjjahalgae.domain.contract.mapper.ContractMapper;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.contract.usecase.get.list.GetContractListUseCaseImpl;
import me.jinjjahalgae.domain.contract.usecase.get.list.dto.ContractListResponse;
import me.jinjjahalgae.domain.participation.enums.Role;
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
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GetContractListUseCaseTest {

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private ContractMapper contractMapper;

    @InjectMocks
    private GetContractListUseCaseImpl contractListUseCase;

    private User user;
    private Contract contract1;
    private Contract contract2;
    private ContractListResponse response1;
    private ContractListResponse response2;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("테스트 사용자")
                .build();

        contract1 = Contract.builder()
                .user(user)
                .title("매일 운동하기")
                .goal("매일 30분 운동")
                .penalty("치킨 못 먹기")
                .reward("치킨 먹기")
                .life(3)
                .proofPerWeek(7)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .type(ContractType.BASIC)
                .oneOff(false)
                .build();

        contract2 = Contract.builder()
                .user(user)
                .title("금연하기")
                .goal("담배 안 피우기")
                .penalty("용돈 줄이기")
                .reward("맛있는 음식 먹기")
                .life(3)
                .proofPerWeek(7)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .type(ContractType.BASIC)
                .oneOff(false)
                .build();

        response1 = new ContractListResponse(
                1L, "uuid1", "매일 운동하기", ContractStatus.IN_PROGRESS, 7,
                LocalDateTime.now(), LocalDateTime.now().plusDays(30),
                "치킨 먹기", "치킨 못 먹기", "10/30", "15/30", 33.3, 50.0
        );

        response2 = new ContractListResponse(
                2L, "uuid2", "금연하기", ContractStatus.PENDING, 7,
                LocalDateTime.now(), LocalDateTime.now().plusDays(30),
                "맛있는 음식 먹기", "용돈 줄이기", "0/30", "0/30", 0.0, 0.0
        );

        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("계약자 역할로 계약 목록 조회 성공")
    void getContractList_Success_AsContractor() {
        // Given
        List<Contract> contracts = Arrays.asList(contract1, contract2);
        Page<Contract> contractPage = new PageImpl<>(contracts, pageable, contracts.size());
        List<ContractStatus> expectedStatuses = List.of(ContractStatus.PENDING, ContractStatus.IN_PROGRESS);

        given(contractRepository.findByUserIdAndStatusInOrderByIdDesc(1L, expectedStatuses, pageable))
                .willReturn(contractPage);
        given(contractMapper.toListResponse(contract1)).willReturn(response1);
        given(contractMapper.toListResponse(contract2)).willReturn(response2);

        // When
        Page<ContractListResponse> result = contractListUseCase.execute(1L, Role.CONTRACTOR, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).title()).isEqualTo("매일 운동하기");
        assertThat(result.getContent().get(1).title()).isEqualTo("금연하기");

        verify(contractRepository).findByUserIdAndStatusInOrderByIdDesc(1L, expectedStatuses, pageable);
        verify(contractMapper).toListResponse(contract1);
        verify(contractMapper).toListResponse(contract2);
    }

    @Test
    @DisplayName("감독자 역할로 계약 목록 조회 성공")
    void getContractList_Success_AsSupervisor() {
        // Given
        List<Contract> contracts = Arrays.asList(contract1);
        Page<Contract> contractPage = new PageImpl<>(contracts, pageable, contracts.size());
        List<ContractStatus> expectedStatuses = List.of(ContractStatus.PENDING, ContractStatus.IN_PROGRESS);

        given(contractRepository.findContractByParticipantUserIdAndRoleAndStatusInOrderByIdDesc(
                1L, Role.SUPERVISOR, expectedStatuses, pageable))
                .willReturn(contractPage);
        given(contractMapper.toListResponse(contract1)).willReturn(response1);

        // When
        Page<ContractListResponse> result = contractListUseCase.execute(1L, Role.SUPERVISOR, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).title()).isEqualTo("매일 운동하기");

        verify(contractRepository).findContractByParticipantUserIdAndRoleAndStatusInOrderByIdDesc(
                1L, Role.SUPERVISOR, expectedStatuses, pageable);
        verify(contractMapper).toListResponse(contract1);
    }

    @Test
    @DisplayName("빈 결과 반환 성공")
    void getContractList_Success_EmptyResult() {
        // Given
        List<Contract> contracts = Arrays.asList();
        Page<Contract> contractPage = new PageImpl<>(contracts, pageable, contracts.size());
        List<ContractStatus> expectedStatuses = List.of(ContractStatus.PENDING, ContractStatus.IN_PROGRESS);

        given(contractRepository.findByUserIdAndStatusInOrderByIdDesc(1L, expectedStatuses, pageable))
                .willReturn(contractPage);

        // When
        Page<ContractListResponse> result = contractListUseCase.execute(1L, Role.CONTRACTOR, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();

        verify(contractRepository).findByUserIdAndStatusInOrderByIdDesc(1L, expectedStatuses, pageable);
    }
}