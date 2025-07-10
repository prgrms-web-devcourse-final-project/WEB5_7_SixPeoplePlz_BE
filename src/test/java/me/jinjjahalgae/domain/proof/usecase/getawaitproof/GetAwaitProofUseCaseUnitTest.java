package me.jinjjahalgae.domain.proof.usecase.getawaitproof;

import me.jinjjahalgae.domain.participation.repository.ParticipationRepository;
import me.jinjjahalgae.domain.proof.entities.Proof;
import me.jinjjahalgae.domain.proof.enums.ProofStatus;
import me.jinjjahalgae.domain.proof.repository.ProofRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAwaitProofUseCaseUnitTest {

    @Mock
    private ProofRepository proofRepository;

    @Mock
    private ParticipationRepository participationRepository;

    @InjectMocks
    private GetAwaitProofUseCaseImpl getAwaitProofUseCase;

    private Long contractId = 1L;
    private Long userId = 1L;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("대기 중인 인증 목록 조회 성공")
    void execute_Success() {
        // given
        List<Long> proofIds = Arrays.asList(1L, 2L, 3L);
        List<Proof> proofs = Arrays.asList(
                ProofTestUtil.createProof(1L, "인증1", ProofStatus.APPROVED, contractId, null, LocalDateTime.now()),
                ProofTestUtil.createProof(2L, "인증2", ProofStatus.APPROVED, contractId, null, LocalDateTime.now()),
                ProofTestUtil.createProof(3L, "인증3", ProofStatus.APPROVED, contractId, null, LocalDateTime.now())
        );

        when(participationRepository.existsByContractIdAndUserId(contractId, userId)).thenReturn(true);
        when(proofRepository.findPendingProofIdsWithoutUserFeedback(contractId, userId)).thenReturn(proofIds);
        when(proofRepository.findProofsWithProofImagesByIds(proofIds)).thenReturn(proofs);

        // when
        List<ProofAwaitResponse> result = getAwaitProofUseCase.execute(contractId, userId);

        // then
        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("사용자가 계약의 참여자가 아니면 예외 발생")
    void execute_ThrowsException_WhenNotParticipant() {
        // given
        when(participationRepository.existsByContractIdAndUserId(contractId, userId)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> getAwaitProofUseCase.execute(contractId, userId))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("계약에 대한 접근 권한이 없습니다.");
    }

    @Test
    @DisplayName("대기 중인 인증이 없을 때 빈 리스트 반환")
    void execute_ReturnsEmptyList_WhenNoAwaitProofs() {
        // given
        when(participationRepository.existsByContractIdAndUserId(contractId, userId)).thenReturn(true);
        when(proofRepository.findPendingProofIdsWithoutUserFeedback(contractId, userId)).thenReturn(List.of());
        when(proofRepository.findProofsWithProofImagesByIds(any())).thenReturn(List.of());

        // when
        List<ProofAwaitResponse> result = getAwaitProofUseCase.execute(contractId, userId);

        // then
        assertThat(result).isEmpty();
    }
} 