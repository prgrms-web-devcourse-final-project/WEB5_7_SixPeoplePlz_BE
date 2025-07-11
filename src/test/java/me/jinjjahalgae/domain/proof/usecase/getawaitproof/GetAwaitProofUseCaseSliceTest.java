package me.jinjjahalgae.domain.proof.usecase.getawaitproof;

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
import me.jinjjahalgae.domain.proof.usecase.get.await.GetAwaitProofUseCaseImpl;
import me.jinjjahalgae.domain.proof.usecase.get.await.dto.ProofAwaitResponse;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(GetAwaitProofUseCaseImpl.class)
@Transactional
@ActiveProfiles("test")
class GetAwaitProofUseCaseSliceTest {

    @Autowired
    GetAwaitProofUseCaseImpl getAwaitProofUseCase;

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
    TestEntityManager entityManager;

    Long contractId;
    Long userId;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .name("test")
                .nickname("testNickname")
                .build();

        User savedUser = userRepository.save(user);
        userId = savedUser.getId();

        Contract contract = ProofTestUtil.createContractBeforeSave(3, savedUser);
        Contract savedContract = contractRepository.save(contract);
        contractId = savedContract.getId();

        Participation participation = ProofTestUtil.createParticipationBeforeSave(savedContract, savedUser);
        participationRepository.save(participation);
    }

    @Test
    @DisplayName("대기 중인 인증 목록 조회 성공 - 슬라이스 테스트")
    void execute_Success() {
        // given
        Proof proof1 = ProofTestUtil.createProofBeforeSave("인증1", ProofStatus.APPROVE_PENDING, contractId, null);
        Proof savedProof1 = proofRepository.save(proof1);

        ProofImage image1 = ProofTestUtil.createProofImageBeforeSave(savedProof1, 1);
        ProofImage savedImage1 = proofImageRepository.save(image1);

        ProofImage image2 = ProofTestUtil.createProofImageBeforeSave(savedProof1, 2);
        ProofImage savedImage2 = proofImageRepository.save(image2);

        savedProof1.addProofImage(savedImage1);
        savedProof1.addProofImage(savedImage2);

        Proof proof2 = ProofTestUtil.createProofBeforeSave("인증2", ProofStatus.APPROVE_PENDING, contractId, null);
        Proof savedProof2 = proofRepository.save(proof2);

        ProofImage image3 = ProofTestUtil.createProofImageBeforeSave(savedProof2, 1);
        ProofImage savedImage3 = proofImageRepository.save(image3);

        savedProof2.addProofImage(savedImage3);

        // when
        List<ProofAwaitResponse> result = getAwaitProofUseCase.execute(contractId, userId);

        // then
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("사용자가 계약의 참여자가 아니면 예외 발생 - 슬라이스 테스트")
    void execute_ThrowsException_WhenNotParticipant() {
        // given
        Long nonParticipantUserId = 999L;

        // when & then
        assertThatThrownBy(() -> getAwaitProofUseCase.execute(contractId, nonParticipantUserId))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("계약에 대한 접근 권한이 없습니다.");
    }

    @Test
    @DisplayName("대기 중인 인증이 없을 때 빈 리스트 반환 - 슬라이스 테스트")
    void execute_ReturnsEmptyList_WhenNoAwaitProofs() {
        // when
        List<ProofAwaitResponse> result = getAwaitProofUseCase.execute(contractId, userId);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("승인한(피드백을 준) 인증은 대기 목록에 포함되지 않음 - 슬라이스 테스트")
    void execute_ExcludesApprovedProofs() {
        // given
        Proof approvedProof = ProofTestUtil.createProofBeforeSave("승인된 인증", ProofStatus.APPROVED, contractId, null);
        Proof savedApprovedProof = proofRepository.save(approvedProof);
        ProofImage image1 = ProofTestUtil.createProofImageBeforeSave(savedApprovedProof, 1);
        ProofImage savedImage1 = proofImageRepository.save(image1);

        savedApprovedProof.addProofImage(savedImage1);

        Proof pendingProofWithFeedback = ProofTestUtil.createProofBeforeSave("대기 중인 인증(피드백 완료)", ProofStatus.APPROVE_PENDING, contractId, null);
        Proof savedPendingProofWithFeedback = proofRepository.save(pendingProofWithFeedback);
        ProofImage image2 = ProofTestUtil.createProofImageBeforeSave(savedPendingProofWithFeedback, 1);
        ProofImage savedImage2 = proofImageRepository.save(image2);

        savedPendingProofWithFeedback.addProofImage(savedImage2);

        Proof pendingProof = ProofTestUtil.createProofBeforeSave("대기 중인 인증", ProofStatus.APPROVE_PENDING, contractId, null);
        Proof savedPendingProof = proofRepository.save(pendingProof);
        ProofImage image3 = ProofTestUtil.createProofImageBeforeSave(savedPendingProof, 1);
        ProofImage savedImage3 = proofImageRepository.save(image3);

        savedPendingProof.addProofImage(savedImage3);

        Feedback feedbackApprovedProof = ProofTestUtil.createFeedbackBeforeSave(null, FeedbackStatus.APPROVED, userId, savedApprovedProof);
        Feedback feedbackPendingProof = ProofTestUtil.createFeedbackBeforeSave(null, FeedbackStatus.APPROVED, userId, savedPendingProofWithFeedback);
        feedbackRepository.save(feedbackApprovedProof);
        feedbackRepository.save(feedbackPendingProof);

        // when
        List<ProofAwaitResponse> result = getAwaitProofUseCase.execute(contractId, userId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).comment()).isEqualTo("대기 중인 인증");
    }
} 