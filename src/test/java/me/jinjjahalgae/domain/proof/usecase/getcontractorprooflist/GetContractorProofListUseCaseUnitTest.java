package me.jinjjahalgae.domain.proof.usecase.getcontractorprooflist;

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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetContractorProofListUseCaseUnitTest {

    @Mock
    private ProofRepository proofRepository;

    @Mock
    private ContractRepository contractRepository;

    @InjectMocks
    private GetContractorProofListUseCaseImpl getContractorProofListUseCase;

    private Long contractId = 1L;
    private Long userId = 1L;
    private int year = 2025;
    private int month = 7;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("계약자 인증 목록 조회 성공")
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

        when(contractRepository.existsByIdAndUserId(contractId, userId)).thenReturn(true);
        when(proofRepository.findOriginalProofIdsByMonth(eq(contractId), any(), any())).thenReturn(proofIds);
        when(proofRepository.findProofsWithProofImagesByIds(proofIds)).thenReturn(proofs);
        when(proofRepository.findReProofIdsByMonth(eq(contractId), any(), any())).thenReturn(reProofIds);
        when(proofRepository.findProofsWithProofImagesByIds(reProofIds)).thenReturn(reProofs);

        // when
        List<ContractorProofListResponse> result = getContractorProofListUseCase.execute(contractId, year, month, userId);

        // then
        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("사용자의 계약이 아니면 예외 발생")
    void execute_ThrowsException_WhenNotUserContract() {
        // given
        when(contractRepository.existsByIdAndUserId(contractId, userId)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> getContractorProofListUseCase.execute(contractId, year, month, userId))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("계약에 대한 접근 권한이 없습니다.");
    }

    @Test
    @DisplayName("인증이 없을 때 빈 리스트 반환")
    void execute_ReturnsEmptyList_WhenNoProofs() {
        // given
        when(contractRepository.existsByIdAndUserId(contractId, userId)).thenReturn(true);
        when(proofRepository.findOriginalProofIdsByMonth(eq(contractId), any(), any())).thenReturn(List.of());
        when(proofRepository.findProofsWithProofImagesByIds(any())).thenReturn(List.of());
        when(proofRepository.findReProofIdsByMonth(eq(contractId), any(), any())).thenReturn(List.of());

        // when
        List<ContractorProofListResponse> result = getContractorProofListUseCase.execute(contractId, year, month, userId);

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

        when(contractRepository.existsByIdAndUserId(contractId, userId)).thenReturn(true);
        when(proofRepository.findOriginalProofIdsByMonth(eq(contractId), any(), any())).thenReturn(proofIds);
        when(proofRepository.findProofsWithProofImagesByIds(proofIds)).thenReturn(proofs);
        when(proofRepository.findReProofIdsByMonth(eq(contractId), any(), any())).thenReturn(List.of());
        when(proofRepository.findProofsWithProofImagesByIds(List.of())).thenReturn(List.of());

        // when
        List<ContractorProofListResponse> result = getContractorProofListUseCase.execute(contractId, year, month, userId);

        // then
        assertThat(result).hasSize(3);
    }
} 