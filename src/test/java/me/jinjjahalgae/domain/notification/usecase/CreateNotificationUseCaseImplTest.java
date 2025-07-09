package me.jinjjahalgae.domain.notification.usecase;

import lombok.extern.slf4j.Slf4j;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.notification.dto.NotificationCreateRequest;
import me.jinjjahalgae.domain.notification.enums.NotificationType;
import me.jinjjahalgae.domain.notification.repository.NotificationRepository;
import me.jinjjahalgae.domain.participation.dto.response.ParticipantInfoResponse;
import me.jinjjahalgae.domain.participation.usecase.GetParticipantInfoByContractIdUseCaseImpl;
import me.jinjjahalgae.domain.user.User;
import me.jinjjahalgae.domain.user.dto.MyInfoResponse;
import me.jinjjahalgae.domain.user.usecase.GetMyInfoUseCaseImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.ArgumentCaptor;

@Slf4j
class CreateNotificationUseCaseImplTest {

    @Mock
    private NotificationRepository notificationRepository;
    
    @Mock
    private ContractRepository contractRepository;
    
    @Mock
    private GetParticipantInfoByContractIdUseCaseImpl getParticipantInfoByContractId;
    
    @Mock
    private GetMyInfoUseCaseImpl getMyInfo;

    @InjectMocks
    private CreateNotificationUseCaseImpl createNotificationUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void SUPERVISOR_ADDED_타입으로_알림을_정상적으로_생성한다() {
        // given
        Long contractId = 1L;
        Long actorUserId = 2L;
        NotificationType type = NotificationType.SUPERVISOR_ADDED;
        
        log.info("=== 테스트 시작: {} 타입 알림 생성 ===", type);
        
        // Mock 데이터 설정
        User actorUser = NotificationTestUtil.createUser(actorUserId, "감독자");
        MyInfoResponse actorInfo = NotificationTestUtil.createMyInfoResponse(actorUserId, "감독자");
        Contract contract = NotificationTestUtil.createContract(contractId, actorUser);
        List<ParticipantInfoResponse> participants = NotificationTestUtil.createParticipantList();
        
        log.info("생성된 Actor User: id={}, name={}", actorUser.getId(), actorUser.getName());
        log.info("생성된 Contract: id={}, title={}", contract.getId(), contract.getTitle());
        log.info("참여자 목록 크기: {}", participants.size());
        
        // Mock 설정
        when(getMyInfo.execute(actorUserId)).thenReturn(actorInfo);
        when(contractRepository.findContractById(contractId)).thenReturn(Optional.of(contract));
        when(getParticipantInfoByContractId.execute(contractId)).thenReturn(participants);
        when(notificationRepository.saveAll(anyList())).thenReturn(List.of());
        
        log.info("Mock 설정 완료: getMyInfo, contractRepository, getParticipantInfoByContractId, notificationRepository");
        
        NotificationCreateRequest request = NotificationTestUtil.createNotificationRequest(type, contractId, actorUserId);
        log.info("생성된 Request: type={}, contractId={}, actorUserId={}", 
                request.type(), request.contractId(), request.actorUserId());

        // when
        log.info("=== UseCase 실행 시작 ===");
        createNotificationUseCase.execute(request);
        log.info("=== UseCase 실행 완료 ===");

        // then
        log.info("=== 검증 시작 ===");
        
        // ArgumentCaptor를 사용해서 실제 저장되는 알림 리스트 캡처
        ArgumentCaptor<List<me.jinjjahalgae.domain.notification.entities.Notification>> notificationCaptor = 
                ArgumentCaptor.forClass(List.class);
        verify(notificationRepository).saveAll(notificationCaptor.capture());
        
        List<me.jinjjahalgae.domain.notification.entities.Notification> savedNotifications = notificationCaptor.getValue();
        
        // 계약자에게만 알림이 가야 함 (1명)
        assertThat(savedNotifications).hasSize(1);
        log.info("✅ 알림 개수 검증 통과: 예상 1개, 실제 {}개", savedNotifications.size());
        
        // 메시지 검증
        String expectedMessage = "'감독자'님이 '테스트 계약' 계약의 감독자로 추가되었습니다.";
        assertThat(savedNotifications.get(0).getContent()).isEqualTo(expectedMessage);
        log.info("✅ 메시지 검증 통과: '{}'", savedNotifications.get(0).getContent());
        
        log.info("=== 모든 테스트 검증 완료 ===");
    }

    @Test
    void CONTRACT_STARTED_타입으로_알림을_정상적으로_생성한다() {
        // given
        Long contractId = 1L;
        Long actorUserId = 1L;
        NotificationType type = NotificationType.CONTRACT_STARTED;
        
        log.info("=== 테스트 시작: {} 타입 알림 생성 ===", type);
        
        // Mock 데이터 설정
        User actorUser = NotificationTestUtil.createUser(actorUserId, "계약자");
        MyInfoResponse actorInfo = NotificationTestUtil.createMyInfoResponse(actorUserId, "계약자");
        Contract contract = NotificationTestUtil.createContract(contractId, actorUser);
        List<ParticipantInfoResponse> participants = NotificationTestUtil.createParticipantList();
        
        log.info("생성된 Actor User: id={}, name={}", actorUser.getId(), actorUser.getName());
        log.info("생성된 Contract: id={}, title={}", contract.getId(), contract.getTitle());
        log.info("참여자 목록 크기: {}", participants.size());
        
        // Mock 설정
        when(getMyInfo.execute(actorUserId)).thenReturn(actorInfo);
        when(contractRepository.findContractById(contractId)).thenReturn(Optional.of(contract));
        when(getParticipantInfoByContractId.execute(contractId)).thenReturn(participants);
        when(notificationRepository.saveAll(anyList())).thenReturn(List.of());
        
        log.info("Mock 설정 완료: getMyInfo, contractRepository, getParticipantInfoByContractId, notificationRepository");
        
        NotificationCreateRequest request = NotificationTestUtil.createNotificationRequest(type, contractId, actorUserId);
        log.info("생성된 Request: type={}, contractId={}, actorUserId={}", 
                request.type(), request.contractId(), request.actorUserId());

        // when
        log.info("=== UseCase 실행 시작 ===");
        createNotificationUseCase.execute(request);
        log.info("=== UseCase 실행 완료 ===");

        // then
        log.info("=== 검증 시작 ===");
        
        // ArgumentCaptor를 사용해서 실제 저장되는 알림 리스트 캡처
        ArgumentCaptor<List<me.jinjjahalgae.domain.notification.entities.Notification>> notificationCaptor = 
                ArgumentCaptor.forClass(List.class);
        verify(notificationRepository).saveAll(notificationCaptor.capture());
        
        List<me.jinjjahalgae.domain.notification.entities.Notification> savedNotifications = notificationCaptor.getValue();
        
        // 모든 참여자에게 알림이 가야 함 (3명)
        assertThat(savedNotifications).hasSize(3);
        log.info("✅ 알림 개수 검증 통과: 예상 3개, 실제 {}개", savedNotifications.size());
        
        // 메시지 검증
        String expectedMessage = "'계약자'님의 '테스트 계약' 계약이 시작되었습니다.";
        assertThat(savedNotifications.get(0).getContent()).isEqualTo(expectedMessage);
        log.info("✅ 메시지 검증 통과: '{}'", savedNotifications.get(0).getContent());
        
        log.info("=== 모든 테스트 검증 완료 ===");
    }

    @Test
    void PROOF_ADDED_타입으로_알림을_정상적으로_생성한다() {
        // given
        Long contractId = 1L;
        Long actorUserId = 1L;
        NotificationType type = NotificationType.PROOF_ADDED;
        
        log.info("=== 테스트 시작: {} 타입 알림 생성 ===", type);
        
        // Mock 데이터 설정
        User actorUser = NotificationTestUtil.createUser(actorUserId, "계약자");
        MyInfoResponse actorInfo = NotificationTestUtil.createMyInfoResponse(actorUserId, "계약자");
        Contract contract = NotificationTestUtil.createContract(contractId, actorUser);
        List<ParticipantInfoResponse> participants = NotificationTestUtil.createParticipantList();
        
        log.info("생성된 Actor User: id={}, name={}", actorUser.getId(), actorUser.getName());
        log.info("생성된 Contract: id={}, title={}", contract.getId(), contract.getTitle());
        log.info("참여자 목록 크기: {}", participants.size());
        
        // Mock 설정
        when(getMyInfo.execute(actorUserId)).thenReturn(actorInfo);
        when(contractRepository.findContractById(contractId)).thenReturn(Optional.of(contract));
        when(getParticipantInfoByContractId.execute(contractId)).thenReturn(participants);
        when(notificationRepository.saveAll(anyList())).thenReturn(List.of());
        
        log.info("Mock 설정 완료: getMyInfo, contractRepository, getParticipantInfoByContractId, notificationRepository");
        
        NotificationCreateRequest request = NotificationTestUtil.createNotificationRequest(type, contractId, actorUserId);
        log.info("생성된 Request: type={}, contractId={}, actorUserId={}", 
                request.type(), request.contractId(), request.actorUserId());

        // when
        log.info("=== UseCase 실행 시작 ===");
        createNotificationUseCase.execute(request);
        log.info("=== UseCase 실행 완료 ===");

        // then
        log.info("=== 검증 시작 ===");
        
        // ArgumentCaptor를 사용해서 실제 저장되는 알림 리스트 캡처
        ArgumentCaptor<List<me.jinjjahalgae.domain.notification.entities.Notification>> notificationCaptor = 
                ArgumentCaptor.forClass(List.class);
        verify(notificationRepository).saveAll(notificationCaptor.capture());
        
        List<me.jinjjahalgae.domain.notification.entities.Notification> savedNotifications = notificationCaptor.getValue();
        
        // 감독자들에게만 알림이 가야 함 (2명)
        assertThat(savedNotifications).hasSize(2);
        log.info("✅ 알림 개수 검증 통과: 예상 2개, 실제 {}개", savedNotifications.size());
        
        // 메시지 검증
        String expectedMessage = "'계약자'님이 '테스트 계약' 계약의 인증을 등록하였습니다.";
        assertThat(savedNotifications.get(0).getContent()).isEqualTo(expectedMessage);
        log.info("✅ 메시지 검증 통과: '{}'", savedNotifications.get(0).getContent());
        
        log.info("=== 모든 테스트 검증 완료 ===");
    }

    @Test
    void SUPERVISOR_ADDED_타입으로_무효한감독자가_있을때_알림을_정상적으로_생성한다() {
        // given
        Long contractId = 1L;
        Long actorUserId = 2L;
        NotificationType type = NotificationType.SUPERVISOR_ADDED;
        
        log.info("=== 테스트 시작: {} 타입 알림 생성 (무효한 감독자 포함) ===", type);
        
        // Mock 데이터 설정
        User actorUser = NotificationTestUtil.createUser(actorUserId, "감독자");
        MyInfoResponse actorInfo = NotificationTestUtil.createMyInfoResponse(actorUserId, "감독자");
        Contract contract = NotificationTestUtil.createContract(contractId, actorUser);
        List<ParticipantInfoResponse> participants = NotificationTestUtil.createParticipantListWithInvalidSupervisor();
        
        log.info("생성된 Actor User: id={}, name={}", actorUser.getId(), actorUser.getName());
        log.info("생성된 Contract: id={}, title={}", contract.getId(), contract.getTitle());
        log.info("참여자 목록 크기: {}", participants.size());
        log.info("참여자 상세: 계약자(validate=true), 유효한감독자(validate=true), 무효한감독자(validate=false)");
        
        // Mock 설정
        when(getMyInfo.execute(actorUserId)).thenReturn(actorInfo);
        when(contractRepository.findContractById(contractId)).thenReturn(Optional.of(contract));
        when(getParticipantInfoByContractId.execute(contractId)).thenReturn(participants);
        when(notificationRepository.saveAll(anyList())).thenReturn(List.of());
        
        log.info("Mock 설정 완료: getMyInfo, contractRepository, getParticipantInfoByContractId, notificationRepository");
        
        NotificationCreateRequest request = NotificationTestUtil.createNotificationRequest(type, contractId, actorUserId);
        log.info("생성된 Request: type={}, contractId={}, actorUserId={}", 
                request.type(), request.contractId(), request.actorUserId());

        // when
        log.info("=== UseCase 실행 시작 ===");
        createNotificationUseCase.execute(request);
        log.info("=== UseCase 실행 완료 ===");

        // then
        log.info("=== 검증 시작 ===");
        
        // ArgumentCaptor를 사용해서 실제 저장되는 알림 리스트 캡처
        ArgumentCaptor<List<me.jinjjahalgae.domain.notification.entities.Notification>> notificationCaptor = 
                ArgumentCaptor.forClass(List.class);
        verify(notificationRepository).saveAll(notificationCaptor.capture());
        
        List<me.jinjjahalgae.domain.notification.entities.Notification> savedNotifications = notificationCaptor.getValue();
        
        // 계약자에게만 알림이 가야 함 (1명) - 감독자 추가는 계약자에게만 알림
        assertThat(savedNotifications).hasSize(1);
        log.info("✅ 알림 개수 검증 통과: 예상 1개, 실제 {}개 (계약자에게만 알림)", savedNotifications.size());
        
        // 메시지 검증
        String expectedMessage = "'감독자'님이 '테스트 계약' 계약의 감독자로 추가되었습니다.";
        assertThat(savedNotifications.get(0).getContent()).isEqualTo(expectedMessage);
        log.info("✅ 메시지 검증 통과: '{}'", savedNotifications.get(0).getContent());
        
        log.info("=== 모든 테스트 검증 완료 ===");
    }

    @Test
    void PROOF_ADDED_타입으로_무효한감독자가_있을때_알림을_정상적으로_생성한다() {
        // given
        Long contractId = 1L;
        Long actorUserId = 1L;
        NotificationType type = NotificationType.PROOF_ADDED;
        
        log.info("=== 테스트 시작: {} 타입 알림 생성 (무효한 감독자 포함) ===", type);
        
        // Mock 데이터 설정
        User actorUser = NotificationTestUtil.createUser(actorUserId, "계약자");
        MyInfoResponse actorInfo = NotificationTestUtil.createMyInfoResponse(actorUserId, "계약자");
        Contract contract = NotificationTestUtil.createContract(contractId, actorUser);
        List<ParticipantInfoResponse> participants = NotificationTestUtil.createParticipantListWithInvalidSupervisor();
        
        log.info("생성된 Actor User: id={}, name={}", actorUser.getId(), actorUser.getName());
        log.info("생성된 Contract: id={}, title={}", contract.getId(), contract.getTitle());
        log.info("참여자 목록 크기: {}", participants.size());
        log.info("참여자 상세: 계약자(validate=true), 유효한감독자(validate=true), 무효한감독자(validate=false)");
        
        // Mock 설정
        when(getMyInfo.execute(actorUserId)).thenReturn(actorInfo);
        when(contractRepository.findContractById(contractId)).thenReturn(Optional.of(contract));
        when(getParticipantInfoByContractId.execute(contractId)).thenReturn(participants);
        when(notificationRepository.saveAll(anyList())).thenReturn(List.of());
        
        log.info("Mock 설정 완료: getMyInfo, contractRepository, getParticipantInfoByContractId, notificationRepository");
        
        NotificationCreateRequest request = NotificationTestUtil.createNotificationRequest(type, contractId, actorUserId);
        log.info("생성된 Request: type={}, contractId={}, actorUserId={}", 
                request.type(), request.contractId(), request.actorUserId());

        // when
        log.info("=== UseCase 실행 시작 ===");
        createNotificationUseCase.execute(request);
        log.info("=== UseCase 실행 완료 ===");

        // then
        log.info("=== 검증 시작 ===");
        
        // ArgumentCaptor를 사용해서 실제 저장되는 알림 리스트 캡처
        ArgumentCaptor<List<me.jinjjahalgae.domain.notification.entities.Notification>> notificationCaptor = 
                ArgumentCaptor.forClass(List.class);
        verify(notificationRepository).saveAll(notificationCaptor.capture());
        
        List<me.jinjjahalgae.domain.notification.entities.Notification> savedNotifications = notificationCaptor.getValue();
        
        // 유효한 감독자들에게만 알림이 가야 함 (1명) - 무효한 감독자는 제외
        assertThat(savedNotifications).hasSize(1);
        log.info("✅ 알림 개수 검증 통과: 예상 1개, 실제 {}개 (무효한 감독자 제외)", savedNotifications.size());
        
        // 메시지 검증
        String expectedMessage = "'계약자'님이 '테스트 계약' 계약의 인증을 등록하였습니다.";
        assertThat(savedNotifications.get(0).getContent()).isEqualTo(expectedMessage);
        log.info("✅ 메시지 검증 통과: '{}'", savedNotifications.get(0).getContent());
        
        log.info("=== 모든 테스트 검증 완료 ===");
    }
}