package me.jinjjahalgae.domain.contract.usecase.interfaces;

import lombok.extern.slf4j.Slf4j;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import me.jinjjahalgae.domain.contract.enums.ContractType;
import me.jinjjahalgae.domain.contract.mapper.ContractMapper;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.contract.usecase.get.preview.GetContractPreviewUseCaseImpl;
import me.jinjjahalgae.domain.contract.usecase.get.preview.dto.ContractPreviewResponse;
import me.jinjjahalgae.domain.participation.entity.Participation;
import me.jinjjahalgae.domain.participation.enums.Role;
import me.jinjjahalgae.domain.participation.repository.ParticipationRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class GetContractPreviewUseCaseImplTest {

    @Mock
    private ParticipationRepository participationRepository;

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private ContractMapper contractMapper;

    @InjectMocks
    private GetContractPreviewUseCaseImpl getContractPreviewUseCase;

    private User contractor;
    private User supervisor1;
    private User supervisor2;
    private Contract contract;
    private List<Participation> participations;

    @BeforeEach
    void setUp() {
        log.info("=== 테스트 설정 시작 ===");
        
        // 사용자 생성
        contractor = createUser(1L, "김계약", "contractor@test.com");
        supervisor1 = createUser(2L, "박감독", "supervisor1@test.com");
        supervisor2 = createUser(3L, "이감독", "supervisor2@test.com");
        
        log.info("사용자 생성 완료: 계약자={}, 감독자1={}, 감독자2={}", 
                contractor.getName(), supervisor1.getName(), supervisor2.getName());

        // 계약 생성 (ContractMapper.toEntity 방식 활용)
        contract = createContractWithMapper(contractor, "매일 운동하기", "매일 1시간 이상 운동", 
                LocalDateTime.of(2024, 1, 1, 9, 0), 
                LocalDateTime.of(2024, 1, 31, 23, 59), 
                ContractStatus.IN_PROGRESS);
        
        log.info("계약 생성 완료: id={}, 제목={}, 상태={}", 
                contract.getId(), contract.getTitle(), contract.getStatus());

        // 참여자 목록 생성
        participations = createParticipations(contract, contractor, supervisor1, supervisor2);
        
        log.info("참여자 목록 생성 완료: {}명", participations.size());
        
        // 생성된 객체들의 상세 내용 로그 출력
        logContractDetails(contract, "생성된 계약 상세 정보");
        logParticipationDetails(participations, "생성된 참여자 목록");
        
        log.info("=== 테스트 설정 완료 ===\n");
    }

    @Test
    @DisplayName("계약자로 계약 미리보기 조회 성공")
    void getContractPreview_AsContractor_Success() {
        // given
        Long userId = contractor.getId();
        Long contractId = contract.getId();
        
        log.info("=== 계약자 미리보기 테스트 시작 ===");
        log.info("요청 정보: userId={}, contractId={}", userId, contractId);

        // Mock 설정
        when(contractRepository.findContractById(contractId))
                .thenReturn(Optional.of(contract));
        log.info("계약 조회 Mock 설정 완료");

        when(participationRepository.existsByContractIdAndUserIdAndValidIsTrue(contractId, userId))
                .thenReturn(true);
        log.info("참여자 존재 여부 Mock 설정 완료");

        when(participationRepository.findByContractId(contractId))
                .thenReturn(participations);
        log.info("참여자 목록 조회 Mock 설정 완료");

        ContractPreviewResponse expectedResponse = createExpectedResponse(contract, participations);
        when(contractMapper.mapToContractPreviewResponse(contract, participations))
                .thenReturn(expectedResponse);
        log.info("매퍼 Mock 설정 완료");

        // when
        log.info("실행: getContractPreviewUseCase.execute()");
        ContractPreviewResponse result = getContractPreviewUseCase.execute(userId, contractId);
        log.info("실행 완료: result={}", result);

        // then
        log.info("검증 시작");
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedResponse);
        
        // 결과 내용 상세 로그 출력
        logContractPreviewDetails(result, "계약자 미리보기 결과");
        
        verify(contractRepository).findContractById(contractId);
        verify(participationRepository).existsByContractIdAndUserIdAndValidIsTrue(contractId, userId);
        verify(participationRepository).findByContractId(contractId);
        verify(contractMapper).mapToContractPreviewResponse(contract, participations);
        
        log.info("검증 완료: 모든 Mock 메서드가 정상 호출됨");
        log.info("=== 계약자 미리보기 테스트 완료 ===\n");
    }

    @Test
    @DisplayName("감독자로 계약 미리보기 조회 성공")
    void getContractPreview_AsSupervisor_Success() {
        // given
        Long userId = supervisor1.getId();
        Long contractId = contract.getId();
        
        log.info("=== 감독자 미리보기 테스트 시작 ===");
        log.info("요청 정보: userId={}, contractId={}", userId, contractId);

        // Mock 설정
        when(contractRepository.findContractById(contractId))
                .thenReturn(Optional.of(contract));
        log.info("계약 조회 Mock 설정 완료");

        when(participationRepository.existsByContractIdAndUserIdAndValidIsTrue(contractId, userId))
                .thenReturn(true);
        log.info("참여자 존재 여부 Mock 설정 완료");

        when(participationRepository.findByContractId(contractId))
                .thenReturn(participations);
        log.info("참여자 목록 조회 Mock 설정 완료");

        ContractPreviewResponse expectedResponse = createExpectedResponse(contract, participations);
        when(contractMapper.mapToContractPreviewResponse(contract, participations))
                .thenReturn(expectedResponse);
        log.info("매퍼 Mock 설정 완료");

        // when
        log.info("실행: getContractPreviewUseCase.execute()");
        ContractPreviewResponse result = getContractPreviewUseCase.execute(userId, contractId);
        log.info("실행 완료: result={}", result);

        // then
        log.info("검증 시작");
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedResponse);
        
        // 결과 내용 상세 로그 출력
        logContractPreviewDetails(result, "감독자 미리보기 결과");
        
        verify(contractRepository).findContractById(contractId);
        verify(participationRepository).existsByContractIdAndUserIdAndValidIsTrue(contractId, userId);
        verify(participationRepository).findByContractId(contractId);
        verify(contractMapper).mapToContractPreviewResponse(contract, participations);
        
        log.info("검증 완료: 모든 Mock 메서드가 정상 호출됨");
        log.info("=== 감독자 미리보기 테스트 완료 ===\n");
    }

    @Test
    @DisplayName("존재하지 않는 계약 조회 시 예외 발생")
    void getContractPreview_ContractNotFound_ThrowsException() {
        // given
        Long userId = contractor.getId();
        Long contractId = 999L;
        
        log.info("=== 존재하지 않는 계약 테스트 시작 ===");
        log.info("요청 정보: userId={}, contractId={}", userId, contractId);

        when(contractRepository.findContractById(contractId))
                .thenReturn(Optional.empty());
        log.info("계약 조회 Mock 설정 완료 (빈 결과)");

        // when & then
        log.info("실행 및 예외 검증: getContractPreviewUseCase.execute()");
        assertThatThrownBy(() -> getContractPreviewUseCase.execute(userId, contractId))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("존재하지 않는 계약입니다.");
        
        log.info("예외 검증 완료: CONTRACT_NOT_FOUND 예외 발생");
        
        verify(contractRepository).findContractById(contractId);
        verify(participationRepository, never()).existsByContractIdAndUserIdAndValidIsTrue(anyLong(), anyLong());
        verify(participationRepository, never()).findByContractId(anyLong());
        verify(contractMapper, never()).mapToContractPreviewResponse(any(), any());
        
        log.info("검증 완료: 계약 조회 후 다른 메서드는 호출되지 않음");
        log.info("=== 존재하지 않는 계약 테스트 완료 ===\n");
    }

    @Test
    @DisplayName("참여자가 아닌 사용자 조회 시 예외 발생")
    void getContractPreview_NotParticipant_ThrowsException() {
        // given
        Long userId = 999L; // 참여하지 않은 사용자
        Long contractId = contract.getId();
        
        log.info("=== 참여자가 아닌 사용자 테스트 시작 ===");
        log.info("요청 정보: userId={}, contractId={}", userId, contractId);

        when(contractRepository.findContractById(contractId))
                .thenReturn(Optional.of(contract));
        log.info("계약 조회 Mock 설정 완료");

        when(participationRepository.existsByContractIdAndUserIdAndValidIsTrue(contractId, userId))
                .thenReturn(false);
        log.info("참여자 존재 여부 Mock 설정 완료 (false)");

        // when & then
        log.info("실행 및 예외 검증: getContractPreviewUseCase.execute()");
        assertThatThrownBy(() -> getContractPreviewUseCase.execute(userId, contractId))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("계약에 대한 접근 권한이 없습니다.");
        
        log.info("예외 검증 완료: ACCESS_DENIED 예외 발생");
        
        verify(contractRepository).findContractById(contractId);
        verify(participationRepository).existsByContractIdAndUserIdAndValidIsTrue(contractId, userId);
        verify(participationRepository, never()).findByContractId(anyLong());
        verify(contractMapper, never()).mapToContractPreviewResponse(any(), any());
        
        log.info("검증 완료: 참여자 확인 후 다른 메서드는 호출되지 않음");
        log.info("=== 참여자가 아닌 사용자 테스트 완료 ===\n");
    }

    @Test
    @DisplayName("다양한 계약 상태에서 미리보기 조회 성공")
    void getContractPreview_DifferentContractStatuses_Success() {
        log.info("=== 다양한 계약 상태 테스트 시작 ===");
        
        // 각 상태별로 테스트
        ContractStatus[] statuses = {ContractStatus.PENDING, ContractStatus.IN_PROGRESS, 
                                   ContractStatus.COMPLETED, ContractStatus.FAILED, ContractStatus.ABANDONED};
        
        for (ContractStatus status : statuses) {
            log.info("테스트 중인 계약 상태: {}", status);
            
            // given
            Contract testContract = createContractWithMapper(contractor, 
                    "테스트 계약 - " + status, "테스트 목표", 
                    LocalDateTime.now(), LocalDateTime.now().plusDays(30), status);
            Long userId = contractor.getId();
            Long contractId = testContract.getId();
            
            // Mock 설정
            when(contractRepository.findContractById(contractId))
                    .thenReturn(Optional.of(testContract));
            when(participationRepository.existsByContractIdAndUserIdAndValidIsTrue(contractId, userId))
                    .thenReturn(true);
            when(participationRepository.findByContractId(contractId))
                    .thenReturn(participations);
            
            ContractPreviewResponse expectedResponse = createExpectedResponse(testContract, participations);
            when(contractMapper.mapToContractPreviewResponse(testContract, participations))
                    .thenReturn(expectedResponse);
            
            // when
            ContractPreviewResponse result = getContractPreviewUseCase.execute(userId, contractId);
            
            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(expectedResponse);
            
            // 결과 내용 상세 로그 출력
            logContractPreviewDetails(result, "상태 " + status + " 미리보기 결과");
            
            log.info("상태 {} 테스트 완료", status);
        }
        
        log.info("=== 다양한 계약 상태 테스트 완료 ===\n");
    }

    // ========== 헬퍼 메서드들 ==========
    
    /**
     * ContractPreviewResponse의 상세 내용을 로그로 출력하는 헬퍼 메서드
     * @param response 출력할 ContractPreviewResponse
     * @param title 로그 제목
     */
    private void logContractPreviewDetails(ContractPreviewResponse response, String title) {
        log.info("=== {} ===", title);
        
        if (response == null) {
            log.info("응답이 null입니다.");
            return;
        }
        
        // 기본 계약 정보
        if (response.contractBasicResponse() != null) {
            log.info("📋 계약 기본 정보:");
            log.info("  - 계약 ID: {}", response.contractBasicResponse().contractId());
            log.info("  - 계약 UUID: {}", response.contractBasicResponse().contractUuid());
            log.info("  - 제목: {}", response.contractBasicResponse().title());
            log.info("  - 목표: {}", response.contractBasicResponse().goal());
            log.info("  - 벌칙: {}", response.contractBasicResponse().penalty());
            log.info("  - 보상: {}", response.contractBasicResponse().reward());
            log.info("  - 주간 인증 횟수: {}", response.contractBasicResponse().proofPerWeek());
            log.info("  - 총 인증 횟수: {}", response.contractBasicResponse().totalProof());
            log.info("  - 실패 가능 횟수: {}", response.contractBasicResponse().totalLife());
            log.info("  - 시작일: {}", response.contractBasicResponse().startDate());
            log.info("  - 종료일: {}", response.contractBasicResponse().endDate());
        } else {
            log.info("📋 계약 기본 정보: null");
        }
        
        // 계약 타입
        log.info("🎨 계약 타입: {}", response.type());
        
        // 참여자 정보
        if (response.participants() != null && !response.participants().isEmpty()) {
            log.info("👥 참여자 정보 ({}명):", response.participants().size());
            for (int i = 0; i < response.participants().size(); i++) {
                var participant = response.participants().get(i);
                log.info("  {}. 사용자 ID: {}, 이름: {}, 역할: {}, 서명 이미지: {}", 
                        i + 1, 
                        participant.basicInfo().userId(),
                        participant.basicInfo().userName(),
                        participant.basicInfo().role(),
                        participant.signatureImageKey());
            }
        } else {
            log.info("👥 참여자 정보: 없음 또는 null");
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
        
        log.info("📄 계약 상세 정보:");
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
        log.info("  - 참여자 수: {}", contract.getParticipations().size());
        
        log.info("=== {} 완료 ===", title);
    }
    
    /**
     * Participation 리스트의 상세 내용을 로그로 출력하는 헬퍼 메서드
     * @param participations 출력할 Participation 리스트
     * @param title 로그 제목
     */
    private void logParticipationDetails(List<Participation> participations, String title) {
        log.info("=== {} ===", title);
        
        if (participations == null || participations.isEmpty()) {
            log.info("참여자 목록이 null이거나 비어있습니다.");
            return;
        }
        
        log.info("👥 참여자 목록 ({}명):", participations.size());
        for (int i = 0; i < participations.size(); i++) {
            Participation participation = participations.get(i);
            log.info("  {}. 참여 ID: {}, 사용자: {} (ID: {}), 역할: {}, 서명 이미지: {}, 유효: {}", 
                    i + 1,
                    participation.getId(),
                    participation.getUser() != null ? participation.getUser().getName() : "null",
                    participation.getUser() != null ? participation.getUser().getId() : "null",
                    participation.getRole(),
                    participation.getImageKey(),
                    participation.getValid());
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
            idField.set(contract, 1L);
            
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

    private List<Participation> createParticipations(Contract contract, User contractor, User supervisor1, User supervisor2) {
        return List.of(
                Participation.builder()
                        .contract(contract)
                        .user(contractor)
                        .role(Role.CONTRACTOR)
                        .imageKey("contractor-signature-123.jpg")
                        .valid(true)
                        .build(),
                Participation.builder()
                        .contract(contract)
                        .user(supervisor1)
                        .role(Role.SUPERVISOR)
                        .imageKey("supervisor-signature-456.jpg")
                        .valid(true)
                        .build(),
                Participation.builder()
                        .contract(contract)
                        .user(supervisor2)
                        .role(Role.SUPERVISOR)
                        .imageKey("supervisor-signature-789.jpg")
                        .valid(true)
                        .build()
        );
    }

    private ContractPreviewResponse createExpectedResponse(Contract contract, List<Participation> participations) {
        // ContractMapper.mapToContractPreviewResponse의 실제 구현에 맞춰서 생성
        // 실제로는 매퍼에서 ContractBasicResponse와 ParticipantFullResponse 리스트를 생성
        return new ContractPreviewResponse(
                null, // ContractBasicResponse는 매퍼에서 생성
                contract.getType(),
                List.of() // ParticipantFullResponse 리스트는 매퍼에서 생성
        );
    }
}
