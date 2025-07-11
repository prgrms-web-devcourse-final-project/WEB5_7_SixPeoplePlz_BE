package me.jinjjahalgae.domain.proof.usecase.getsupervisorprooflist;

import jakarta.persistence.EntityManager;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.feedback.entity.Feedback;
import me.jinjjahalgae.domain.feedback.enums.FeedbackStatus;
import me.jinjjahalgae.domain.feedback.repository.FeedbackRepository;
import me.jinjjahalgae.domain.participation.entity.Participation;
import me.jinjjahalgae.domain.participation.repository.ParticipationRepository;
import me.jinjjahalgae.domain.proof.entities.Proof;
import me.jinjjahalgae.domain.proof.entities.ProofImage;
import me.jinjjahalgae.domain.proof.enums.ProofStatus;
import me.jinjjahalgae.domain.proof.repository.ProofImageRepository;
import me.jinjjahalgae.domain.proof.repository.ProofRepository;
import me.jinjjahalgae.domain.proof.usecase.getlist.supervisorlist.GetSupervisorProofListUseCaseImpl;
import me.jinjjahalgae.domain.proof.usecase.getlist.supervisorlist.dto.SupervisorProofListResponse;
import me.jinjjahalgae.domain.proof.util.ProofTestUtil;
import me.jinjjahalgae.domain.user.User;
import me.jinjjahalgae.domain.user.UserRepository;
import me.jinjjahalgae.global.exception.AppException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(GetSupervisorProofListUseCaseImpl.class)
@ActiveProfiles("test")
class GetSupervisorProofListUseCaseSliceTest {

    @Autowired
    GetSupervisorProofListUseCaseImpl getSupervisorProofListUseCase;

    @Autowired
    ProofRepository proofRepository;

    @Autowired
    ParticipationRepository participationRepository;

    @Autowired
    ContractRepository contractRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProofImageRepository proofImageRepository;

    @Autowired
    FeedbackRepository feedbackRepository;

    @Autowired
    EntityManager entityManager;

    Long contractId;
    Long userId1;
    Long userId2;
    Long userId3;
    int year = 2025;
    int month = 7;

    @BeforeEach
    void setUp() {
        User user1 = User.builder()
                .name("test1")
                .nickname("testNickname1")
                .build();

        User savedUser1 = userRepository.save(user1);

        User user2 = User.builder()
                .name("test2")
                .nickname("testNickname2")
                .build();

        User savedUser2 = userRepository.save(user2);

        User user3 = User.builder()
                .name("test3")
                .nickname("testNickname3")
                .build();

        User savedUser3 = userRepository.save(user3);


        userId1 = savedUser1.getId();
        userId2 = savedUser2.getId();
        userId3 = savedUser3.getId();

        Contract contract = ProofTestUtil.createContractBeforeSave(3, savedUser1);
        contract = contractRepository.save(contract);
        contractId = contract.getId();

        Participation participation = ProofTestUtil.createParticipationBeforeSave(contract, savedUser1);
        participationRepository.save(participation);
    }

    @Test
    @DisplayName("감독자 인증 목록 조회 성공 - 슬라이스 테스트")
    void execute_Success() {
        // given
        // 인증 1
        Proof proof1 = ProofTestUtil.createProofBeforeSave("인증1", ProofStatus.APPROVED, contractId, null);
        Proof savedProof1 = proofRepository.save(proof1);
        ProofImage image1 = ProofTestUtil.createProofImageBeforeSave(savedProof1, 1);
        ProofImage savedImage1 = proofImageRepository.save(image1);

        savedProof1.addProofImage(savedImage1);

        entityManager.createNativeQuery("UPDATE proof SET created_at = :newCreatedAt WHERE id = :id")
                .setParameter("newCreatedAt", LocalDateTime.of(2025, 7, 9, 0, 0))
                .setParameter("id", savedProof1.getId())
                .executeUpdate();

        entityManager.flush();
        entityManager.clear();

        // 인증 2
        Proof proof2 = ProofTestUtil.createProofBeforeSave("인증2", ProofStatus.APPROVED, contractId, null);
        Proof savedProof2 = proofRepository.save(proof2);
        ProofImage image2 = ProofTestUtil.createProofImageBeforeSave(savedProof2, 1);
        ProofImage savedImage2 = proofImageRepository.save(image2);

        savedProof2.addProofImage(savedImage2);

        // 재인증 1
        Proof reProof1 = ProofTestUtil.createProofBeforeSave("재인증1", ProofStatus.APPROVE_PENDING, contractId, savedProof1.getId());
        Proof savedReProof1 = proofRepository.save(reProof1);
        ProofImage image3 = ProofTestUtil.createProofImageBeforeSave(savedReProof1, 1);
        ProofImage savedImage3 = proofImageRepository.save(image3);

        savedReProof1.addProofImage(savedImage3);


        // 피드백
        Proof newProof = proofRepository.findById(savedProof1.getId()).get();
        Feedback feedback1 = ProofTestUtil.createFeedbackBeforeSave("테스트 코멘트", FeedbackStatus.APPROVED, userId1, newProof);
        Feedback savedFeedback1 = feedbackRepository.save(feedback1);
        savedProof1.addFeedback(savedFeedback1);

        Feedback feedback2 = ProofTestUtil.createFeedbackBeforeSave("테스트 코멘트", FeedbackStatus.APPROVED, userId2, newProof);
        Feedback savedFeedback2 = feedbackRepository.save(feedback2);
        savedProof1.addFeedback(savedFeedback2);

        Feedback feedback3 = ProofTestUtil.createFeedbackBeforeSave("테스트 코멘트", FeedbackStatus.APPROVED, userId3, newProof);
        Feedback savedFeedback3 = feedbackRepository.save(feedback3);
        savedProof1.addFeedback(savedFeedback3);

        Feedback feedback4 = ProofTestUtil.createFeedbackBeforeSave(null, FeedbackStatus.REJECTED, userId1, savedProof2);
        Feedback savedFeedback4 = feedbackRepository.save(feedback4);
        savedProof2.addFeedback(savedFeedback4);

        Feedback feedback5 = ProofTestUtil.createFeedbackBeforeSave(null, FeedbackStatus.REJECTED, userId3, savedProof2);
        Feedback savedFeedback5 = feedbackRepository.save(feedback5);
        savedProof2.addFeedback(savedFeedback5);

        Feedback feedback6 = ProofTestUtil.createFeedbackBeforeSave(null, FeedbackStatus.APPROVED, userId1, savedReProof1);
        Feedback savedFeedback6 = feedbackRepository.save(feedback6);
        savedReProof1.addFeedback(savedFeedback6);

        // when
        List<SupervisorProofListResponse> result = getSupervisorProofListUseCase.execute(contractId, year, month, userId1);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).reProof()).isNotNull();
    }

    @Test
    @DisplayName("사용자가 계약의 참여자가 아니면 예외 발생 - 슬라이스 테스트")
    void execute_ThrowsException_WhenNotParticipant() {
        // given
        Long nonParticipantUserId = 999L;

        // when & then
        assertThatThrownBy(() -> getSupervisorProofListUseCase.execute(contractId, year, month, nonParticipantUserId))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("계약에 대한 접근 권한이 없습니다.");
    }

    @Test
    @DisplayName("인증이 없을 때 빈 리스트 반환 - 슬라이스 테스트")
    void execute_ReturnsEmptyList_WhenNoProofs() {
        // when
        List<SupervisorProofListResponse> result = getSupervisorProofListUseCase.execute(contractId, year, month, userId1);

        // then
        assertThat(result).isEmpty();
    }


    @Test
    @DisplayName("다른 달의 인증은 포함되지 않음 - 슬라이스 테스트")
    void execute_ExcludesProofsFromOtherMonths() {
        // given
        // 이번 달 인증
        Proof proofThisMonth = ProofTestUtil.createProofBeforeSave("이번 달 인증", ProofStatus.APPROVED, contractId, null);
        Proof savedThisMonth = proofRepository.save(proofThisMonth);
        ProofImage image1 = ProofTestUtil.createProofImageBeforeSave(savedThisMonth, 1);
        ProofImage savedImage1 = proofImageRepository.save(image1);

        savedThisMonth.addProofImage(savedImage1);

        // 다른 달 인증
        Proof proofOtherMonth = ProofTestUtil.createProofBeforeSave("다른 달 인증", ProofStatus.APPROVED, contractId, null);
        Proof savedOtherMonth = proofRepository.save(proofOtherMonth);

        // 리플렉션의 setField 가 DB에 반영이 되지 않아 직접 업데이트
        entityManager.createNativeQuery("UPDATE proof SET created_at = :newCreatedAt WHERE id = :id")
                .setParameter("newCreatedAt", LocalDateTime.of(2025, 6, 10, 0, 0))
                .setParameter("id", savedOtherMonth.getId())
                .executeUpdate();

        entityManager.flush();
        entityManager.clear();

        Proof newOtherMonthProof = proofRepository.findById(savedOtherMonth.getId()).get();
        ProofImage image2 = ProofTestUtil.createProofImageBeforeSave(newOtherMonthProof, 1);
        ProofImage savedImage2 = proofImageRepository.save(image2);

        newOtherMonthProof.addProofImage(savedImage2);

        // 피드백

        Feedback feedback1 = ProofTestUtil.createFeedbackBeforeSave(null, FeedbackStatus.APPROVED, userId1, savedThisMonth);
        feedbackRepository.save(feedback1);

        Feedback feedback2 = ProofTestUtil.createFeedbackBeforeSave(null, FeedbackStatus.APPROVED, userId1, newOtherMonthProof);
        feedbackRepository.save(feedback2);

        // when
        List<SupervisorProofListResponse> result = getSupervisorProofListUseCase.execute(contractId, year, month, userId1);

        // then
        assertThat(result).hasSize(1);
    }
} 