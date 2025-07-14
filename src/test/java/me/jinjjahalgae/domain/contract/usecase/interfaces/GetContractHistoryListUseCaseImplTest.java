package me.jinjjahalgae.domain.contract.usecase.interfaces;

import lombok.extern.slf4j.Slf4j;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import me.jinjjahalgae.domain.contract.enums.ContractType;
import me.jinjjahalgae.domain.contract.mapper.ContractMapper;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.contract.usecase.get.historylist.GetContractHistoryListUseCaseImpl;
import me.jinjjahalgae.domain.contract.usecase.get.historylist.dto.ContractHistoryRequest;
import me.jinjjahalgae.domain.contract.usecase.get.list.dto.ContractListResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class GetContractHistoryListUseCaseImplTest {

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private ContractMapper contractMapper;

    @InjectMocks
    private GetContractHistoryListUseCaseImpl getContractHistoryListUseCase;

    private User contractor;
    private User supervisor1;
    private User supervisor2;
    private Contract completedContract;
    private Contract failedContract;
    private Contract abandonedContract;
    private List<Contract> contracts;
    private List<ContractListResponse> contractListResponses;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        log.info("=== 테스트 설정 시작 ===");
        
        // 여러 사용자 생성 (계약자, 감독자들)
        contractor = createUser(1L, "김계약", "contractor@test.com");
        supervisor1 = createUser(2L, "박감독", "supervisor1@test.com");
        supervisor2 = createUser(3L, "이감독", "supervisor2@test.com");
        
        log.info("사용자 생성 완료: 계약자={}, 감독자1={}, 감독자2={}", 
                contractor.getName(), supervisor1.getName(), supervisor2.getName());

        // 계약 생성 (계약자가 만든 계약들)
        completedContract = createContractWithMapper(contractor, "완료된 계약", "목표 달성", 
                LocalDateTime.of(2024, 1, 1, 9, 0), 
                LocalDateTime.of(2024, 1, 31, 23, 59), 
                ContractStatus.COMPLETED);
        
        failedContract = createContractWithMapper(contractor, "실패한 계약", "목표 실패", 
                LocalDateTime.of(2024, 2, 1, 9, 0), 
                LocalDateTime.of(2024, 2, 29, 23, 59), 
                ContractStatus.FAILED);
        
        abandonedContract = createContractWithMapper(contractor, "포기한 계약", "중간 포기", 
                LocalDateTime.of(2024, 3, 1, 9, 0), 
                LocalDateTime.of(2024, 3, 31, 23, 59), 
                ContractStatus.ABANDONED);
        
        // 참여자(계약자, 감독자1, 감독자2) 모두 각 계약에 등록
        addParticipationsToContract(completedContract);
        addParticipationsToContract(failedContract);
        addParticipationsToContract(abandonedContract);
        
        contracts = List.of(completedContract, failedContract, abandonedContract);
        
        log.info("계약 생성 완료: 완료={}, 실패={}, 포기={}", 
                completedContract.getTitle(), failedContract.getTitle(), abandonedContract.getTitle());

        // ContractListResponse 생성
        contractListResponses = createContractListResponses(contracts);
        
        // Pageable 설정
        pageable = PageRequest.of(0, 10);
        
        // 생성된 객체들의 상세 내용 로그 출력
        logContractDetails(completedContract, "완료된 계약 상세 정보");
        logParticipationDetails(completedContract.getParticipations(), "완료된 계약 참여자 목록");
        logContractDetails(failedContract, "실패한 계약 상세 정보");
        logParticipationDetails(failedContract.getParticipations(), "실패한 계약 참여자 목록");
        logContractDetails(abandonedContract, "포기한 계약 상세 정보");
        logParticipationDetails(abandonedContract.getParticipations(), "포기한 계약 참여자 목록");
        
        log.info("=== 테스트 설정 완료 ===\n");
    }

    private void addParticipationsToContract(Contract contract) {
        contract.getParticipations().add(
            me.jinjjahalgae.domain.participation.entity.Participation.builder()
                .contract(contract)
                .user(contractor)
                .role(Role.CONTRACTOR)
                .imageKey("contractor-signature-" + contract.getId() + ".jpg")
                .valid(true)
                .build()
        );
        contract.getParticipations().add(
            me.jinjjahalgae.domain.participation.entity.Participation.builder()
                .contract(contract)
                .user(supervisor1)
                .role(Role.SUPERVISOR)
                .imageKey("supervisor1-signature-" + contract.getId() + ".jpg")
                .valid(true)
                .build()
        );
        contract.getParticipations().add(
            me.jinjjahalgae.domain.participation.entity.Participation.builder()
                .contract(contract)
                .user(supervisor2)
                .role(Role.SUPERVISOR)
                .imageKey("supervisor2-signature-" + contract.getId() + ".jpg")
                .valid(true)
                .build()
        );
    }

    @Test
    @DisplayName("계약자 역할로 히스토리 조회 성공 (모든 상태)")
    void getContractHistory_AsContractor_AllStatuses_Success() {
        // given
        Long userId = contractor.getId();
        ContractHistoryRequest request = new ContractHistoryRequest("CONTRACTOR", null, null, null);
        
        log.info("=== 계약자 히스토리 조회 테스트 시작 ===");
        log.info("요청 정보: userId={}, role={}, keyword={}, endDate={}, status={}", 
                userId, request.role(), request.keyword(), request.endDate(), request.status());

        // Mock 설정
        Page<Contract> contractPage = new PageImpl<>(contracts, pageable, contracts.size());
        when(contractRepository.findContractHistoryByConditions(
                eq(userId), eq(Role.CONTRACTOR), eq(null), eq(null), eq(null), eq(pageable)))
                .thenReturn(contractPage);
        log.info("계약 조회 Mock 설정 완료");

        when(contractMapper.toListResponse(any(Contract.class)))
                .thenAnswer(invocation -> {
                    Contract contract = invocation.getArgument(0);
                    return contractListResponses.stream()
                            .filter(response -> response.contractId().equals(contract.getId()))
                            .findFirst()
                            .orElse(null);
                });
        log.info("매퍼 Mock 설정 완료");

        // when
        log.info("실행: getContractHistoryListUseCase.execute()");
        Page<ContractListResponse> result = getContractHistoryListUseCase.execute(userId, request, pageable);
        log.info("실행 완료: result size={}", result.getContent().size());

        // then
        log.info("검증 시작");
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getTotalElements()).isEqualTo(3);
        
        // 결과 내용 상세 로그 출력
        logContractHistoryDetails(result, "계약자 히스토리 조회 결과");
        
        verify(contractRepository).findContractHistoryByConditions(
                eq(userId), eq(Role.CONTRACTOR), eq(null), eq(null), eq(null), eq(pageable));
        verify(contractMapper, times(3)).toListResponse(any(Contract.class));
        
        log.info("검증 완료: 모든 Mock 메서드가 정상 호출됨");
        log.info("=== 계약자 히스토리 조회 테스트 완료 ===\n");
    }

    @Test
    @DisplayName("감독자 역할로 히스토리 조회 성공 (완료된 계약만)")
    void getContractHistory_AsSupervisor_CompletedOnly_Success() {
        // given
        Long userId = supervisor1.getId(); // 감독자 1이 완료된 계약을 감독
        ContractHistoryRequest request = new ContractHistoryRequest("SUPERVISOR", null, null, "COMPLETED");
        
        log.info("=== 감독자 히스토리 조회 테스트 시작 ===");
        log.info("요청 정보: userId={}, role={}, keyword={}, endDate={}, status={}", 
                userId, request.role(), request.keyword(), request.endDate(), request.status());

        // Mock 설정
        Page<Contract> contractPage = new PageImpl<>(List.of(completedContract), pageable, 1);
        when(contractRepository.findContractHistoryByConditions(
                eq(userId), eq(Role.SUPERVISOR), eq(null), eq(null), eq(ContractStatus.COMPLETED), eq(pageable)))
                .thenReturn(contractPage);
        log.info("계약 조회 Mock 설정 완료");

        when(contractMapper.toListResponse(completedContract))
                .thenReturn(contractListResponses.get(0));
        log.info("매퍼 Mock 설정 완료");

        // when
        log.info("실행: getContractHistoryListUseCase.execute()");
        Page<ContractListResponse> result = getContractHistoryListUseCase.execute(userId, request, pageable);
        log.info("실행 완료: result size={}", result.getContent().size());

        // then
        log.info("검증 시작");
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).contractStatus()).isEqualTo(ContractStatus.COMPLETED);
        
        // 결과 내용 상세 로그 출력
        logContractHistoryDetails(result, "감독자 히스토리 조회 결과 (완료된 계약만)");
        
        verify(contractRepository).findContractHistoryByConditions(
                eq(userId), eq(Role.SUPERVISOR), eq(null), eq(null), eq(ContractStatus.COMPLETED), eq(pageable));
        verify(contractMapper).toListResponse(completedContract);
        
        log.info("검증 완료: 모든 Mock 메서드가 정상 호출됨");
        log.info("=== 감독자 히스토리 조회 테스트 완료 ===\n");
    }

    @Test
    @DisplayName("키워드 검색으로 히스토리 조회 성공")
    void getContractHistory_WithKeyword_Success() {
        // given
        Long userId = supervisor2.getId(); // 감독자 2가 키워드 검색
        ContractHistoryRequest request = new ContractHistoryRequest("SUPERVISOR", "완료", null, null);
        
        log.info("=== 키워드 검색 히스토리 조회 테스트 시작 ===");
        log.info("요청 정보: userId={}, role={}, keyword={}, endDate={}, status={}", 
                userId, request.role(), request.keyword(), request.endDate(), request.status());

        // Mock 설정
        Page<Contract> contractPage = new PageImpl<>(List.of(completedContract), pageable, 1);
        when(contractRepository.findContractHistoryByConditions(
                eq(userId), eq(Role.SUPERVISOR), eq("완료"), eq(null), eq(null), eq(pageable)))
                .thenReturn(contractPage);
        log.info("계약 조회 Mock 설정 완료");

        when(contractMapper.toListResponse(completedContract))
                .thenReturn(contractListResponses.get(0));
        log.info("매퍼 Mock 설정 완료");

        // when
        log.info("실행: getContractHistoryListUseCase.execute()");
        Page<ContractListResponse> result = getContractHistoryListUseCase.execute(userId, request, pageable);
        log.info("실행 완료: result size={}", result.getContent().size());

        // then
        log.info("검증 시작");
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).title()).contains("완료");
        
        // 결과 내용 상세 로그 출력
        logContractHistoryDetails(result, "키워드 검색 히스토리 조회 결과");
        
        verify(contractRepository).findContractHistoryByConditions(
                eq(userId), eq(Role.SUPERVISOR), eq("완료"), eq(null), eq(null), eq(pageable));
        verify(contractMapper).toListResponse(completedContract);
        
        log.info("검증 완료: 모든 Mock 메서드가 정상 호출됨");
        log.info("=== 키워드 검색 히스토리 조회 테스트 완료 ===\n");
    }

    @Test
    @DisplayName("종료일 필터로 히스토리 조회 성공")
    void getContractHistory_WithEndDate_Success() {
        // given
        Long userId = supervisor1.getId(); // 감독자 1이 종료일 필터
        LocalDateTime endDate = LocalDateTime.of(2024, 2, 15, 0, 0);
        ContractHistoryRequest request = new ContractHistoryRequest("SUPERVISOR", null, endDate, null);
        
        log.info("=== 종료일 필터 히스토리 조회 테스트 시작 ===");
        log.info("요청 정보: userId={}, role={}, keyword={}, endDate={}, status={}", 
                userId, request.role(), request.keyword(), request.endDate(), request.status());

        // Mock 설정
        Page<Contract> contractPage = new PageImpl<>(List.of(completedContract, failedContract), pageable, 2);
        when(contractRepository.findContractHistoryByConditions(
                eq(userId), eq(Role.SUPERVISOR), eq(null), eq(endDate), eq(null), eq(pageable)))
                .thenReturn(contractPage);
        log.info("계약 조회 Mock 설정 완료");

        when(contractMapper.toListResponse(any(Contract.class)))
                .thenAnswer(invocation -> {
                    Contract contract = invocation.getArgument(0);
                    return contractListResponses.stream()
                            .filter(response -> response.contractId().equals(contract.getId()))
                            .findFirst()
                            .orElse(null);
                });
        log.info("매퍼 Mock 설정 완료");

        // when
        log.info("실행: getContractHistoryListUseCase.execute()");
        Page<ContractListResponse> result = getContractHistoryListUseCase.execute(userId, request, pageable);
        log.info("실행 완료: result size={}", result.getContent().size());

        // then
        log.info("검증 시작");
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        
        // 결과 내용 상세 로그 출력
        logContractHistoryDetails(result, "종료일 필터 히스토리 조회 결과");
        
        verify(contractRepository).findContractHistoryByConditions(
                eq(userId), eq(Role.SUPERVISOR), eq(null), eq(endDate), eq(null), eq(pageable));
        verify(contractMapper, times(2)).toListResponse(any(Contract.class));
        
        log.info("검증 완료: 모든 Mock 메서드가 정상 호출됨");
        log.info("=== 종료일 필터 히스토리 조회 테스트 완료 ===\n");
    }

    @Test
    @DisplayName("유효하지 않은 status로 조회 시 예외 발생")
    void getContractHistory_InvalidStatus_ThrowsException() {
        // given
        Long userId = supervisor1.getId(); // 감독자 1이 유효하지 않은 status 테스트
        ContractHistoryRequest request = new ContractHistoryRequest("SUPERVISOR", null, null, "INVALID_STATUS");
        
        log.info("=== 유효하지 않은 status 테스트 시작 ===");
        log.info("요청 정보: userId={}, role={}, keyword={}, endDate={}, status={}", 
                userId, request.role(), request.keyword(), request.endDate(), request.status());

        // when & then
        log.info("실행 및 예외 검증: getContractHistoryListUseCase.execute()");
        assertThatThrownBy(() -> getContractHistoryListUseCase.execute(userId, request, pageable))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("잘못된 요청입니다.");
        
        log.info("예외 검증 완료: INVALID_REQUEST 예외 발생");
        
        verify(contractRepository, never()).findContractHistoryByConditions(anyLong(), any(), any(), any(), any(), any());
        verify(contractMapper, never()).toListResponse(any(Contract.class));
        
        log.info("검증 완료: Repository와 Mapper는 호출되지 않음");
        log.info("=== 유효하지 않은 status 테스트 완료 ===\n");
    }

    @Test
    @DisplayName("허용되지 않은 status로 조회 시 예외 발생")
    void getContractHistory_NotAllowedStatus_ThrowsException() {
        // given
        Long userId = supervisor1.getId(); // 감독자 1이 허용되지 않은 status 테스트
        ContractHistoryRequest request = new ContractHistoryRequest("SUPERVISOR", null, null, "PENDING");
        
        log.info("=== 허용되지 않은 status 테스트 시작 ===");
        log.info("요청 정보: userId={}, role={}, keyword={}, endDate={}, status={}", 
                userId, request.role(), request.keyword(), request.endDate(), request.status());

        // when & then
        log.info("실행 및 예외 검증: getContractHistoryListUseCase.execute()");
        assertThatThrownBy(() -> getContractHistoryListUseCase.execute(userId, request, pageable))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("잘못된 요청입니다.");
        
        log.info("예외 검증 완료: INVALID_REQUEST 예외 발생");
        
        verify(contractRepository, never()).findContractHistoryByConditions(anyLong(), any(), any(), any(), any(), any());
        verify(contractMapper, never()).toListResponse(any(Contract.class));
        
        log.info("검증 완료: Repository와 Mapper는 호출되지 않음");
        log.info("=== 허용되지 않은 status 테스트 완료 ===\n");
    }

    @Test
    @DisplayName("빈 키워드로 조회 시 null로 처리")
    void getContractHistory_EmptyKeyword_HandledAsNull() {
        // given
        Long userId = contractor.getId(); // 계약자가 빈 키워드 테스트
        ContractHistoryRequest request = new ContractHistoryRequest("CONTRACTOR", "   ", null, null);
        
        log.info("=== 빈 키워드 테스트 시작 ===");
        log.info("요청 정보: userId={}, role={}, keyword={}, endDate={}, status={}", 
                userId, request.role(), request.keyword(), request.endDate(), request.status());

        // Mock 설정
        Page<Contract> contractPage = new PageImpl<>(contracts, pageable, contracts.size());
        when(contractRepository.findContractHistoryByConditions(
                eq(userId), eq(Role.CONTRACTOR), eq(null), eq(null), eq(null), eq(pageable)))
                .thenReturn(contractPage);
        log.info("계약 조회 Mock 설정 완료 (키워드 null로 처리됨)");

        when(contractMapper.toListResponse(any(Contract.class)))
                .thenAnswer(invocation -> {
                    Contract contract = invocation.getArgument(0);
                    return contractListResponses.stream()
                            .filter(response -> response.contractId().equals(contract.getId()))
                            .findFirst()
                            .orElse(null);
                });
        log.info("매퍼 Mock 설정 완료");

        // when
        log.info("실행: getContractHistoryListUseCase.execute()");
        Page<ContractListResponse> result = getContractHistoryListUseCase.execute(userId, request, pageable);
        log.info("실행 완료: result size={}", result.getContent().size());

        // then
        log.info("검증 시작");
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3);
        
        // 결과 내용 상세 로그 출력
        logContractHistoryDetails(result, "빈 키워드 처리 결과");
        
        verify(contractRepository).findContractHistoryByConditions(
                eq(userId), eq(Role.CONTRACTOR), eq(null), eq(null), eq(null), eq(pageable));
        verify(contractMapper, times(3)).toListResponse(any(Contract.class));
        
        log.info("검증 완료: 빈 키워드가 null로 처리되어 모든 계약 조회됨");
        log.info("=== 빈 키워드 테스트 완료 ===\n");
    }

    @Test
    @DisplayName("감독자 역할로 실패한 계약만 조회 성공")
    void getContractHistory_AsSupervisor_FailedOnly_Success() {
        // given
        Long userId = supervisor2.getId(); // 감독자 2가 실패한 계약만 조회
        ContractHistoryRequest request = new ContractHistoryRequest("SUPERVISOR", null, null, "FAILED");
        
        log.info("=== 감독자 실패 계약 조회 테스트 시작 ===");
        log.info("요청 정보: userId={}, role={}, keyword={}, endDate={}, status={}", 
                userId, request.role(), request.keyword(), request.endDate(), request.status());

        // Mock 설정
        Page<Contract> contractPage = new PageImpl<>(List.of(failedContract), pageable, 1);
        when(contractRepository.findContractHistoryByConditions(
                eq(userId), eq(Role.SUPERVISOR), eq(null), eq(null), eq(ContractStatus.FAILED), eq(pageable)))
                .thenReturn(contractPage);
        log.info("계약 조회 Mock 설정 완료");

        when(contractMapper.toListResponse(failedContract))
                .thenReturn(contractListResponses.get(1));
        log.info("매퍼 Mock 설정 완료");

        // when
        log.info("실행: getContractHistoryListUseCase.execute()");
        Page<ContractListResponse> result = getContractHistoryListUseCase.execute(userId, request, pageable);
        log.info("실행 완료: result size={}", result.getContent().size());

        // then
        log.info("검증 시작");
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).contractStatus()).isEqualTo(ContractStatus.FAILED);
        assertThat(result.getContent().get(0).title()).contains("실패");
        
        // 결과 내용 상세 로그 출력
        logContractHistoryDetails(result, "감독자 실패 계약 조회 결과");
        
        verify(contractRepository).findContractHistoryByConditions(
                eq(userId), eq(Role.SUPERVISOR), eq(null), eq(null), eq(ContractStatus.FAILED), eq(pageable));
        verify(contractMapper).toListResponse(failedContract);
        
        log.info("검증 완료: 모든 Mock 메서드가 정상 호출됨");
        log.info("=== 감독자 실패 계약 조회 테스트 완료 ===\n");
    }

    @Test
    @DisplayName("감독자 역할로 포기한 계약만 조회 성공")
    void getContractHistory_AsSupervisor_AbandonedOnly_Success() {
        // given
        Long userId = supervisor1.getId(); // 감독자 1이 포기한 계약만 조회
        ContractHistoryRequest request = new ContractHistoryRequest("SUPERVISOR", null, null, "ABANDONED");
        
        log.info("=== 감독자 포기 계약 조회 테스트 시작 ===");
        log.info("요청 정보: userId={}, role={}, keyword={}, endDate={}, status={}", 
                userId, request.role(), request.keyword(), request.endDate(), request.status());

        // Mock 설정
        Page<Contract> contractPage = new PageImpl<>(List.of(abandonedContract), pageable, 1);
        when(contractRepository.findContractHistoryByConditions(
                eq(userId), eq(Role.SUPERVISOR), eq(null), eq(null), eq(ContractStatus.ABANDONED), eq(pageable)))
                .thenReturn(contractPage);
        log.info("계약 조회 Mock 설정 완료");

        when(contractMapper.toListResponse(abandonedContract))
                .thenReturn(contractListResponses.get(2));
        log.info("매퍼 Mock 설정 완료");

        // when
        log.info("실행: getContractHistoryListUseCase.execute()");
        Page<ContractListResponse> result = getContractHistoryListUseCase.execute(userId, request, pageable);
        log.info("실행 완료: result size={}", result.getContent().size());

        // then
        log.info("검증 시작");
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).contractStatus()).isEqualTo(ContractStatus.ABANDONED);
        assertThat(result.getContent().get(0).title()).contains("포기");
        
        // 결과 내용 상세 로그 출력
        logContractHistoryDetails(result, "감독자 포기 계약 조회 결과");
        
        verify(contractRepository).findContractHistoryByConditions(
                eq(userId), eq(Role.SUPERVISOR), eq(null), eq(null), eq(ContractStatus.ABANDONED), eq(pageable));
        verify(contractMapper).toListResponse(abandonedContract);
        
        log.info("검증 완료: 모든 Mock 메서드가 정상 호출됨");
        log.info("=== 감독자 포기 계약 조회 테스트 완료 ===\n");
    }

    @Test
    @DisplayName("감독자 역할로 모든 히스토리 조회 성공")
    void getContractHistory_AsSupervisor_AllStatuses_Success() {
        // given
        Long userId = supervisor2.getId(); // 감독자 2가 모든 히스토리 조회
        ContractHistoryRequest request = new ContractHistoryRequest("SUPERVISOR", null, null, null);
        
        log.info("=== 감독자 전체 히스토리 조회 테스트 시작 ===");
        log.info("요청 정보: userId={}, role={}, keyword={}, endDate={}, status={}", 
                userId, request.role(), request.keyword(), request.endDate(), request.status());

        // Mock 설정
        Page<Contract> contractPage = new PageImpl<>(contracts, pageable, contracts.size());
        when(contractRepository.findContractHistoryByConditions(
                eq(userId), eq(Role.SUPERVISOR), eq(null), eq(null), eq(null), eq(pageable)))
                .thenReturn(contractPage);
        log.info("계약 조회 Mock 설정 완료");

        when(contractMapper.toListResponse(any(Contract.class)))
                .thenAnswer(invocation -> {
                    Contract contract = invocation.getArgument(0);
                    return contractListResponses.stream()
                            .filter(response -> response.contractId().equals(contract.getId()))
                            .findFirst()
                            .orElse(null);
                });
        log.info("매퍼 Mock 설정 완료");

        // when
        log.info("실행: getContractHistoryListUseCase.execute()");
        Page<ContractListResponse> result = getContractHistoryListUseCase.execute(userId, request, pageable);
        log.info("실행 완료: result size={}", result.getContent().size());

        // then
        log.info("검증 시작");
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getTotalElements()).isEqualTo(3);
        
        // 각 상태별 계약이 포함되어 있는지 확인
        assertThat(result.getContent()).anyMatch(response -> response.contractStatus() == ContractStatus.COMPLETED);
        assertThat(result.getContent()).anyMatch(response -> response.contractStatus() == ContractStatus.FAILED);
        assertThat(result.getContent()).anyMatch(response -> response.contractStatus() == ContractStatus.ABANDONED);
        
        // 결과 내용 상세 로그 출력
        logContractHistoryDetails(result, "감독자 전체 히스토리 조회 결과");
        
        verify(contractRepository).findContractHistoryByConditions(
                eq(userId), eq(Role.SUPERVISOR), eq(null), eq(null), eq(null), eq(pageable));
        verify(contractMapper, times(3)).toListResponse(any(Contract.class));
        
        log.info("검증 완료: 모든 Mock 메서드가 정상 호출됨");
        log.info("=== 감독자 전체 히스토리 조회 테스트 완료 ===\n");
    }

    @Test
    @DisplayName("감독자 역할로 복합 조건 조회 성공")
    void getContractHistory_AsSupervisor_ComplexConditions_Success() {
        // given
        Long userId = supervisor1.getId(); // 감독자 1이 복합 조건으로 조회
        LocalDateTime endDate = LocalDateTime.of(2024, 3, 1, 0, 0);
        ContractHistoryRequest request = new ContractHistoryRequest("SUPERVISOR", "실패", endDate, "FAILED");
        
        log.info("=== 감독자 복합 조건 조회 테스트 시작 ===");
        log.info("요청 정보: userId={}, role={}, keyword={}, endDate={}, status={}", 
                userId, request.role(), request.keyword(), request.endDate(), request.status());

        // Mock 설정
        Page<Contract> contractPage = new PageImpl<>(List.of(failedContract), pageable, 1);
        when(contractRepository.findContractHistoryByConditions(
                eq(userId), eq(Role.SUPERVISOR), eq("실패"), eq(endDate), eq(ContractStatus.FAILED), eq(pageable)))
                .thenReturn(contractPage);
        log.info("계약 조회 Mock 설정 완료");

        when(contractMapper.toListResponse(failedContract))
                .thenReturn(contractListResponses.get(1));
        log.info("매퍼 Mock 설정 완료");

        // when
        log.info("실행: getContractHistoryListUseCase.execute()");
        Page<ContractListResponse> result = getContractHistoryListUseCase.execute(userId, request, pageable);
        log.info("실행 완료: result size={}", result.getContent().size());

        // then
        log.info("검증 시작");
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        
        ContractListResponse response = result.getContent().get(0);
        assertThat(response.contractStatus()).isEqualTo(ContractStatus.FAILED);
        assertThat(response.title()).contains("실패");
        assertThat(response.endDate()).isBeforeOrEqualTo(endDate);
        
        // 결과 내용 상세 로그 출력
        logContractHistoryDetails(result, "감독자 복합 조건 조회 결과");
        
        verify(contractRepository).findContractHistoryByConditions(
                eq(userId), eq(Role.SUPERVISOR), eq("실패"), eq(endDate), eq(ContractStatus.FAILED), eq(pageable));
        verify(contractMapper).toListResponse(failedContract);
        
        log.info("검증 완료: 모든 Mock 메서드가 정상 호출됨");
        log.info("=== 감독자 복합 조건 조회 테스트 완료 ===\n");
    }

    // ========== 헬퍼 메서드들 ==========
    
    /**
     * ContractHistory 조회 결과의 상세 내용을 로그로 출력하는 헬퍼 메서드
     * @param result 출력할 Page<ContractListResponse>
     * @param title 로그 제목
     */
    private void logContractHistoryDetails(Page<ContractListResponse> result, String title) {
        log.info("=== {} ===", title);
        
        if (result == null || result.getContent().isEmpty()) {
            log.info("조회 결과가 null이거나 비어있습니다.");
            return;
        }
        
        log.info("📋 계약 히스토리 목록 (총 {}개, 페이지 {}개):", result.getTotalElements(), result.getContent().size());
        log.info("  - 페이지 정보: {} / {}, 크기: {}", result.getNumber(), result.getTotalPages(), result.getSize());
        
        for (int i = 0; i < result.getContent().size(); i++) {
            ContractListResponse response = result.getContent().get(i);
            log.info("  {}. 계약 ID: {}, 제목: {}, 상태: {}", 
                    i + 1, response.contractId(), response.title(), response.contractStatus());
            log.info("     - 기간: {} ~ {}", response.startDate(), response.endDate());
            log.info("     - 인증: {}회/주, 달성률: {}% ({}), 기간률: {}% ({})", 
                    response.proofPerWeek(), 
                    response.achievementPercent(), response.achievementRatio(),
                    response.periodPercent(), response.periodRatio());
            log.info("     - 보상: {}, 벌칙: {}", response.reward(), response.penalty());
        }
        
        log.info("=== {} 완료 ===", title);
    }
    
    /**
     * Contract 엔티티의 상세 내용을 로그로 출력하는 헬퍼 메서드
     * @param contract 출력할 Contract
     * @param title 로그 제목
     */
    private void logContractDetails(Contract contract, String title) {
        log.info("=== {} ===", title);
        
        if (contract == null) {
            log.info("계약이 null입니다.");
            return;
        }
        
        log.info("계약 상세 정보:");
        log.info("  - 계약 ID: {}", contract.getId());
        log.info("  - 계약 UUID: {}", contract.getUuid());
        log.info("  - 제목: {}", contract.getTitle());
        log.info("  - 목표: {}", contract.getGoal());
        log.info("  - 벌칙: {}", contract.getPenalty());
        log.info("  - 보상: {}", contract.getReward());
        log.info("  - 계약 타입: {}", contract.getType());
        log.info("  - 계약 상태: {}", contract.getStatus());
        log.info("  - 주간 인증 횟수: {}", contract.getProofPerWeek());
        log.info("  - 총 인증 횟수: {}", contract.getTotalProof());
        log.info("  - 현재 인증 횟수: {}", contract.getCurrentProof());
        log.info("  - 실패 가능 횟수: {}", contract.getLife());
        log.info("  - 현재 실패 횟수: {}", contract.getCurrentFail());
        log.info("  - 단발성 여부: {}", contract.isOneOff());
        log.info("  - 시작일: {}", contract.getStartDate());
        log.info("  - 종료일: {}", contract.getEndDate());
        
        log.info("=== {} 완료 ===", title);
    }

    private void logParticipationDetails(List<me.jinjjahalgae.domain.participation.entity.Participation> participations, String title) {
        log.info("=== {} ===", title);
        if (participations == null || participations.isEmpty()) {
            log.info("참여자 목록이 null이거나 비어있습니다.");
            return;
        }
        log.info("📋 계약 참여자 목록 (총 {}명):", participations.size());
        for (int i = 0; i < participations.size(); i++) {
            me.jinjjahalgae.domain.participation.entity.Participation participation = participations.get(i);
            log.info("  {}. 참여자 ID: {}, 역할: {}, 유효: {}",
                    i + 1, participation.getUser().getId(), participation.getRole(), participation.isValid());
            log.info("     - 서명 키: {}", participation.getImageKey());
        }
        log.info("=== {} 완료 ===", title);
    }
    
    private User createUser(Long id, String name, String email) {
        return User.builder()
                .id(id)
                .name(name)
                .email(email)
                .build();
    }

    private Contract createContractWithMapper(User user, String title, String goal, 
                                            LocalDateTime startDate, LocalDateTime endDate, ContractStatus status) {
        // ContractMapper.toEntity 방식으로 계약 생성
        Contract contract = Contract.builder()
                .user(user)
                .title(title)
                .goal(goal)
                .penalty("벌칙")
                .reward("보상")
                .life(3)
                .proofPerWeek(7)
                .oneOff(false)
                .type(ContractType.BASIC)
                .startDate(startDate)
                .endDate(endDate)
                .build();
        
        // initialize() 메서드 호출로 기본값 설정
        contract.initialize();
        
        // 테스트용으로 ID와 상태 설정
        try {
            // Reflection을 사용해서 ID 설정 (실제로는 DB에서 생성됨)
            java.lang.reflect.Field idField = Contract.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(contract, (long) (Math.random() * 1000) + 1); // 랜덤 ID
            
            // 상태 설정
            java.lang.reflect.Field statusField = Contract.class.getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(contract, status);
            
            // 테스트용 데이터 설정
            java.lang.reflect.Field currentProofField = Contract.class.getDeclaredField("currentProof");
            currentProofField.setAccessible(true);
            currentProofField.set(contract, 20);
            
            java.lang.reflect.Field currentFailField = Contract.class.getDeclaredField("currentFail");
            currentFailField.setAccessible(true);
            currentFailField.set(contract, 1);
            
        } catch (Exception e) {
            log.warn("Reflection 설정 실패: {}", e.getMessage());
        }
        
        return contract;
    }

    private List<ContractListResponse> createContractListResponses(List<Contract> contracts) {
        return contracts.stream()
                .map(contract -> new ContractListResponse(
                        contract.getId(),
                        contract.getUuid(),
                        contract.getTitle(),
                        contract.getStatus(),
                        contract.getProofPerWeek(),
                        contract.getStartDate(),
                        contract.getEndDate(),
                        contract.getReward(),
                        contract.getPenalty(),
                        contract.calculateAchievementRatio(),
                        contract.calculatePeriodRatio(),
                        contract.calculateAchievementPercent(),
                        contract.calculatePeriodPercent()
                ))
                .toList();
    }
}
