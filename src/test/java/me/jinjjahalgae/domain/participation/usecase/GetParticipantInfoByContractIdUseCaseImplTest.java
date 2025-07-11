//package me.jinjjahalgae.domain.participation.usecase;
//
//import lombok.extern.slf4j.Slf4j;
//import me.jinjjahalgae.domain.participation.usecase.get.participations.ParticipantInfoResponse;
//import me.jinjjahalgae.domain.participation.entity.Participation;
//import me.jinjjahalgae.domain.participation.enums.Role;
//import me.jinjjahalgae.domain.participation.repository.ParticipationRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.when;
//
//@Slf4j
//class GetParticipantInfoByContractIdUseCaseImplTest {
//
//    @Mock
//    private ParticipationRepository participationRepository;
//
//    @InjectMocks
//    private GetParticipantInfoByContractIdUseCaseImpl getParticipantInfoByContractId;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void contractId로_참여자정보_리스트를_정상적으로_조회한다() {
//        // given
//        Long contractId = 1L;
//        log.info("=== 테스트 시작: contractId = {} ===", contractId);
//
//        var user1 = ParticipationTestUtil.createUser(1L, "userA");
//        var user2 = ParticipationTestUtil.createUser(2L, "userB");
//        var user3 = ParticipationTestUtil.createUser(3L, "userC");
//        log.info("생성된 User1: id={}, name={}", user1.getId(), user1.getName());
//        log.info("생성된 User2: id={}, name={}", user2.getId(), user2.getName());
//        log.info("생성된 User3: id={}, name={}", user3.getId(), user3.getName());
//
//        var contract = ParticipationTestUtil.createContract(contractId, user1);
//        log.info("생성된 Contract: id={}, title={}", contract.getId(), contract.getTitle());
//
//        var participation1 = ParticipationTestUtil.createParticipation(1L, contract, user1, Role.SUPERVISOR);
//        var participation2 = ParticipationTestUtil.createParticipation(2L, contract, user2, Role.CONTRACTOR);
//        var participation3 = ParticipationTestUtil.createParticipation(3L, contract, user3, Role.SUPERVISOR);
//        log.info("생성된 Participation1: id={}, userId={}, role={}",
//                participation1.getId(), participation1.getUser().getId(), participation1.getRole());
//        log.info("생성된 Participation2: id={}, userId={}, role={}",
//                participation2.getId(), participation2.getUser().getId(), participation2.getRole());
//        log.info("생성된 Participation3: id={}, userId={}, role={}",
//                participation3.getId(), participation3.getUser().getId(), participation3.getRole());
//
//        List<Participation> participationList = List.of(participation1, participation2, participation3);
//        log.info("Repository에 전달할 Participation 리스트 크기: {}", participationList.size());
//
//        when(participationRepository.findByContract_Id(contractId)).thenReturn(participationList);
//        log.info("Repository Mock 설정 완료: findByContract_Id({}) 호출 시 {}개의 Participation 반환",
//                contractId, participationList.size());
//
//        // when
//        log.info("=== UseCase 실행 시작 ===");
//        List<ParticipantInfoResponse> result = getParticipantInfoByContractId.execute(contractId);
//        log.info("=== UseCase 실행 완료 ===");
//        log.info("반환된 ParticipantInfoResponse 리스트 크기: {}", result.size());
//
//        // then
//        log.info("=== 검증 시작 ===");
//        assertThat(result).hasSize(3);
//        log.info("✅ 리스트 크기 검증 통과: 예상 3개, 실제 {}개", result.size());
//
//        assertThat(result.get(0).userName()).isEqualTo("userA");
//        assertThat(result.get(0).userId()).isEqualTo(1L);
//        assertThat(result.get(0).role()).isEqualTo(Role.SUPERVISOR);
//        log.info("✅ 첫 번째 참여자 검증 통과: userName={}, userId={}, role={}",
//                result.get(0).userName(), result.get(0).userId(), result.get(0).role());
//
//        assertThat(result.get(1).userName()).isEqualTo("userB");
//        assertThat(result.get(1).userId()).isEqualTo(2L);
//        assertThat(result.get(1).role()).isEqualTo(Role.CONTRACTOR);
//        log.info("✅ 두 번째 참여자 검증 통과: userName={}, userId={}, role={}",
//                result.get(1).userName(), result.get(1).userId(), result.get(1).role());
//
//        assertThat(result.get(2).userName()).isEqualTo("userC");
//        assertThat(result.get(2).userId()).isEqualTo(3L);
//        assertThat(result.get(2).role()).isEqualTo(Role.SUPERVISOR);
//        log.info("✅ 세 번째 참여자 검증 통과: userName={}, userId={}, role={}",
//                result.get(2).userName(), result.get(2).userId(), result.get(2).role());
//
//        log.info("=== 모든 테스트 검증 완료 ===");
//    }
//}