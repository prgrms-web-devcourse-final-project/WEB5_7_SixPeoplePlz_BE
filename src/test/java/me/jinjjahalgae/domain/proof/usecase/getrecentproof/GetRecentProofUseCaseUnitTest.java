package me.jinjjahalgae.domain.proof.usecase.getrecentproof;

import me.jinjjahalgae.domain.contract.repository.ContractRepository;
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
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetRecentProofUseCaseUnitTest {

    @Mock
    private ProofRepository proofRepository;

    @Mock
    private ContractRepository contractRepository;

    @InjectMocks
    private GetRecentProofUseCaseImpl getRecentProofUseCase;

    private Long contractId = 1L;
    private Long userId = 1L;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("최근 인증 목록 조회 성공")
    void execute_Success() {
        // given
        List<Long> proofIds = Arrays.asList(1L, 2L, 3L);
        List<Proof> proofs = Arrays.asList(
                ProofTestUtil.createProof(1L, "인증1", ProofStatus.REJECTED, contractId, null, LocalDateTime.of(2025, 7, 1, 0, 0)),
                ProofTestUtil.createProof(2L, "인증2", ProofStatus.REJECTED, contractId, null, LocalDateTime.of(2025, 7, 2, 0, 0)),
                ProofTestUtil.createProof(3L, "인증3", ProofStatus.APPROVE_PENDING, contractId, null, LocalDateTime.of(2025, 7, 3, 0, 0))
        );

        when(contractRepository.existsByIdAndUserId(contractId, userId)).thenReturn(true);
        when(proofRepository.findProofIdsByContractId(eq(contractId), any(PageRequest.class))).thenReturn(proofIds);
        when(proofRepository.findProofsWithProofImagesByIds(proofIds)).thenReturn(proofs);

        // when
        List<ProofRecentResponse> result = getRecentProofUseCase.execute(contractId, userId);

        // then
        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("사용자의 계약이 아니면 예외 발생")
    void execute_ThrowsException_WhenNotUserContract() {
        // given
        when(contractRepository.existsByIdAndUserId(contractId, userId)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> getRecentProofUseCase.execute(contractId, userId))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("계약에 대한 접근 권한이 없습니다.");
    }

    @Test
    @DisplayName("최근 인증이 없을 때 빈 리스트 반환")
    void execute_ReturnsEmptyList_WhenNoRecentProofs() {
        // given
        when(contractRepository.existsByIdAndUserId(contractId, userId)).thenReturn(true);
        when(proofRepository.findProofIdsByContractId(eq(contractId), any(PageRequest.class))).thenReturn(List.of());
        when(proofRepository.findProofsWithProofImagesByIds(any())).thenReturn(List.of());

        // when
        List<ProofRecentResponse> result = getRecentProofUseCase.execute(contractId, userId);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("최근 인증이 3개 미만일 때 성공")
    void execute_Success_WhenLessThanThreeProofs() {
        // given
        List<Long> proofIds = Arrays.asList(1L, 2L);
        List<Proof> proofs = Arrays.asList(
                ProofTestUtil.createProof(1L, "인증1", ProofStatus.REJECTED, contractId, null, LocalDateTime.of(2025, 7, 1, 0, 0)),
                ProofTestUtil.createProof(2L, "인증2", ProofStatus.REJECTED, contractId, null, LocalDateTime.of(2025, 7, 2, 0, 0))
        );

        when(contractRepository.existsByIdAndUserId(contractId, userId)).thenReturn(true);
        when(proofRepository.findProofIdsByContractId(eq(contractId), any(PageRequest.class))).thenReturn(proofIds);
        when(proofRepository.findProofsWithProofImagesByIds(proofIds)).thenReturn(proofs);

        // when
        List<ProofRecentResponse> result = getRecentProofUseCase.execute(contractId, userId);

        // then
        assertThat(result).hasSize(2);
    }
} 