package me.jinjjahalgae.domain.proof.usecase.getsupervisorprooflist;

import me.jinjjahalgae.domain.feedback.entity.Feedback;
import me.jinjjahalgae.domain.feedback.enums.FeedbackStatus;
import me.jinjjahalgae.domain.feedback.repository.FeedbackRepository;
import me.jinjjahalgae.domain.participation.repository.ParticipationRepository;
import me.jinjjahalgae.domain.proof.entities.Proof;
import me.jinjjahalgae.domain.proof.enums.ProofStatus;
import me.jinjjahalgae.domain.proof.repository.ProofRepository;
import me.jinjjahalgae.domain.proof.usecase.getlist.supervisorlist.GetSupervisorProofListUseCaseImpl;
import me.jinjjahalgae.domain.proof.usecase.getlist.supervisorlist.dto.SupervisorProofListResponse;
import me.jinjjahalgae.domain.proof.util.ProofTestUtil;
import me.jinjjahalgae.global.exception.AppException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetSupervisorProofListUseCaseUnitTest {

    @Mock
    private ProofRepository proofRepository;

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private ParticipationRepository participationRepository;

    @InjectMocks
    private GetSupervisorProofListUseCaseImpl getSupervisorProofListUseCase;

    private Long contractId = 1L;
    private Long userId = 1L;
    private int year = 2024;
    private int month = 12;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("감독자 인증 목록 조회 성공")
    void execute_Success() {
        // given
        List<Long> proofIds = Arrays.asList(1L, 2L, 3L);
        List<Long> reProofIds = Arrays.asList(4L, 5L);
        List<Proof> proofs = Arrays.asList(
                ProofTestUtil.createProof(1L, "인증1", ProofStatus.REJECTED, contractId, null, LocalDateTime.of(year, month, 1, 0, 0)),
                ProofTestUtil.createProof(2L, "인증2", ProofStatus.REJECTED, contractId, null, LocalDateTime.of(year, month, 2, 0, 0)),
                ProofTestUtil.createProof(3L, "인증3", ProofStatus.APPROVE_PENDING, contractId, null, LocalDateTime.of(year, month, 3, 0, 0))
        );

        List<Proof> reProofs = Arrays.asList(
                ProofTestUtil.createProof(4L, "재인증1", ProofStatus.APPROVED, contractId, 1L, LocalDateTime.of(year, month, 2, 0, 0)),
                ProofTestUtil.createProof(5L, "재인증2", ProofStatus.APPROVE_PENDING, contractId, 2L, LocalDateTime.of(year, month, 3, 0, 0))
        );

        List<Feedback> feedbacks = Arrays.asList(
                ProofTestUtil.createFeedback(1L, FeedbackStatus.APPROVED, proofs.get(0)),
                ProofTestUtil.createFeedback(2L, FeedbackStatus.REJECTED, proofs.get(1)),
                ProofTestUtil.createFeedback(3L, FeedbackStatus.APPROVED, proofs.get(2))
        );

        when(participationRepository.existsByContractIdAndUserId(contractId, userId)).thenReturn(true);
        when(proofRepository.findOriginalProofIdsByMonthForSupervisor(eq(contractId), any(), any(), eq(userId))).thenReturn(proofIds);
        when(proofRepository.findProofsWithProofImagesByIds(proofIds)).thenReturn(proofs);
        when(proofRepository.findReProofIdsByMonthForSupervisor(eq(contractId), any(), any(), eq(userId))).thenReturn(reProofIds);
        when(proofRepository.findProofsWithProofImagesByIds(reProofIds)).thenReturn(reProofs);
        when(feedbackRepository.findByContractIdAndUserId(contractId, userId)).thenReturn(feedbacks);

        // when
        List<SupervisorProofListResponse> result = getSupervisorProofListUseCase.execute(contractId, year, month, userId);

        // then
        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("사용자가 계약의 참여자가 아니면 예외 발생")
    void execute_ThrowsException_WhenNotParticipant() {
        // given
        when(participationRepository.existsByContractIdAndUserId(contractId, userId)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> getSupervisorProofListUseCase.execute(contractId, year, month, userId))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("계약에 대한 접근 권한이 없습니다.");
    }

    @Test
    @DisplayName("인증이 없을 때 빈 리스트 반환")
    void execute_ReturnsEmptyList_WhenNoProofs() {
        // given
        when(participationRepository.existsByContractIdAndUserId(contractId, userId)).thenReturn(true);
        when(proofRepository.findOriginalProofIdsByMonthForSupervisor(eq(contractId), any(), any(), eq(userId))).thenReturn(List.of());
        when(proofRepository.findProofsWithProofImagesByIds(any())).thenReturn(List.of());
        when(proofRepository.findReProofIdsByMonthForSupervisor(eq(contractId), any(), any(), eq(userId))).thenReturn(List.of());
        when(feedbackRepository.findByContractIdAndUserId(contractId, userId)).thenReturn(List.of());

        // when
        List<SupervisorProofListResponse> result = getSupervisorProofListUseCase.execute(contractId, year, month, userId);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("재인증이 없는 인증 목록 조회 성공")
    void execute_Success_WithoutReProofs() {
        // given
        List<Long> proofIds = Arrays.asList(1L, 2L, 3L);
        List<Proof> proofs = Arrays.asList(
                ProofTestUtil.createProof(1L, "인증1", ProofStatus.REJECTED, contractId, null, LocalDateTime.of(year, month, 1, 0, 0)),
                ProofTestUtil.createProof(2L, "인증2", ProofStatus.REJECTED, contractId, null, LocalDateTime.of(year, month, 2, 0, 0)),
                ProofTestUtil.createProof(3L, "인증3", ProofStatus.APPROVE_PENDING, contractId, null, LocalDateTime.of(year, month, 3, 0, 0))
        );

        List<Feedback> feedbacks = Arrays.asList(
                ProofTestUtil.createFeedback(1L, FeedbackStatus.APPROVED, proofs.get(0)),
                ProofTestUtil.createFeedback(2L, FeedbackStatus.REJECTED, proofs.get(1)),
                ProofTestUtil.createFeedback(3L, FeedbackStatus.APPROVED, proofs.get(2))
        );

        when(participationRepository.existsByContractIdAndUserId(contractId, userId)).thenReturn(true);
        when(proofRepository.findOriginalProofIdsByMonthForSupervisor(eq(contractId), any(), any(), eq(userId))).thenReturn(proofIds);
        when(proofRepository.findProofsWithProofImagesByIds(proofIds)).thenReturn(proofs);
        when(proofRepository.findReProofIdsByMonthForSupervisor(eq(contractId), any(), any(), eq(userId))).thenReturn(List.of());
        when(proofRepository.findProofsWithProofImagesByIds(List.of())).thenReturn(List.of());
        when(feedbackRepository.findByContractIdAndUserId(contractId, userId)).thenReturn(feedbacks);

        // when
        List<SupervisorProofListResponse> result = getSupervisorProofListUseCase.execute(contractId, year, month, userId);

        // then
        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("피드백이 없는 인증 목록 조회 성공")
    void execute_Success_WithoutFeedbacks() {
        // given
        List<Long> proofIds = Arrays.asList(1L, 2L, 3L);
        List<Proof> proofs = Arrays.asList(
                ProofTestUtil.createProof(1L, "인증1", ProofStatus.REJECTED, contractId, null, LocalDateTime.of(year, month, 1, 0, 0)),
                ProofTestUtil.createProof(2L, "인증2", ProofStatus.REJECTED, contractId, null, LocalDateTime.of(year, month, 2, 0, 0)),
                ProofTestUtil.createProof(3L, "인증3", ProofStatus.APPROVE_PENDING, contractId, null, LocalDateTime.of(year, month, 3, 0, 0))
        );

        when(participationRepository.existsByContractIdAndUserId(contractId, userId)).thenReturn(true);
        when(proofRepository.findOriginalProofIdsByMonthForSupervisor(eq(contractId), any(), any(), eq(userId))).thenReturn(proofIds);
        when(proofRepository.findProofsWithProofImagesByIds(proofIds)).thenReturn(proofs);
        when(proofRepository.findReProofIdsByMonthForSupervisor(eq(contractId), any(), any(), eq(userId))).thenReturn(List.of());
        when(proofRepository.findProofsWithProofImagesByIds(List.of())).thenReturn(List.of());
        when(feedbackRepository.findByContractIdAndUserId(contractId, userId)).thenReturn(List.of());

        // when
        List<SupervisorProofListResponse> result = getSupervisorProofListUseCase.execute(contractId, year, month, userId);

        // then
        assertThat(result).hasSize(3);
    }
} 