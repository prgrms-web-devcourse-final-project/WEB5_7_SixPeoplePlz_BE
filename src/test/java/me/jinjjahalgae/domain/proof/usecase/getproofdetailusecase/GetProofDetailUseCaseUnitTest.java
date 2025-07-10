package me.jinjjahalgae.domain.proof.usecase.getproofdetailusecase;

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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetProofDetailUseCaseUnitTest {

    @Mock
    private ProofRepository proofRepository;

    @Mock
    private ParticipationRepository participationRepository;

    @InjectMocks
    private GetProofDetailUseCaseImpl getProofDetailUseCase;

    private Long proofId = 1L;
    private Long contractId = 1L;
    private Long userId = 1L;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("인증 상세 조회 성공")
    void execute_Success() {
        // given
        Proof proof = ProofTestUtil.createProof(proofId, "테스트 인증", ProofStatus.APPROVED, contractId, null, LocalDateTime.now());

        when(proofRepository.findByIdWithProofImagesAndFeedbacks(proofId)).thenReturn(Optional.of(proof));
        when(participationRepository.existsByContractIdAndUserId(contractId, userId)).thenReturn(true);

        // when
        ProofDetailResponse result = getProofDetailUseCase.execute(proofId, userId);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("인증이 존재하지 않으면 예외 발생")
    void execute_ThrowsException_WhenProofNotFound() {
        // given
        when(proofRepository.findByIdWithProofImagesAndFeedbacks(proofId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> getProofDetailUseCase.execute(proofId, userId))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("해당 인증을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("사용자가 계약의 참여자가 아니면 예외 발생")
    void execute_ThrowsException_WhenNotParticipant() {
        // given
        Proof proof = ProofTestUtil.createProof(proofId, "테스트 인증", ProofStatus.APPROVED, contractId, null, LocalDateTime.now());

        when(proofRepository.findByIdWithProofImagesAndFeedbacks(proofId)).thenReturn(Optional.of(proof));
        when(participationRepository.existsByContractIdAndUserId(contractId, userId)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> getProofDetailUseCase.execute(proofId, userId))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("계약에 대한 접근 권한이 없습니다.");
    }
} 