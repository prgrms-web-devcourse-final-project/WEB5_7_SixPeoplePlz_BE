package me.jinjjahalgae.domain.proof.usecase.createproof;

import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.proof.entities.Proof;
import me.jinjjahalgae.domain.proof.entities.ProofImage;
import me.jinjjahalgae.domain.proof.enums.ProofStatus;
import me.jinjjahalgae.domain.proof.repository.ProofImageRepository;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateProofUseCaseUnitTest {

    @Mock
    private ProofRepository proofRepository;

    @Mock
    private ProofImageRepository proofImageRepository;

    @Mock
    private ContractRepository contractRepository;

    @InjectMocks
    private CreateProofUseCaseImpl createProofUseCase;

    private ProofCreateRequest validRequest;
    private Contract contract;
    private Long contractId = 1L;
    private Long userId = 1L;

    @BeforeEach
    void setUp() {
        validRequest = new ProofCreateRequest("image1.jpg", "image2.jpg", null, "테스트 코멘트");

        contract = ProofTestUtil.createContract(contractId, 3);
    }

    @Test
    @DisplayName("인증 생성 성공")
    void execute_Success() {
        // given
        when(contractRepository.existsByIdAndUserId(contractId, userId)).thenReturn(true);
        when(proofRepository.existsByContractIdAndCreatedAtToday(eq(contractId), any(), any())).thenReturn(false);
        when(contractRepository.findById(contractId)).thenReturn(Optional.of(contract));
        when(proofRepository.save(any(Proof.class))).thenReturn(ProofTestUtil.createProof(1L, null, ProofStatus.APPROVED, contractId, null, LocalDateTime.now()));
        when(proofImageRepository.save(any(ProofImage.class))).thenReturn(ProofTestUtil.createProofImage());

        // when
        createProofUseCase.execute(validRequest, contractId, userId);

        // then
        verify(proofRepository).save(any(Proof.class));
        verify(proofImageRepository, times(2)).save(any(ProofImage.class));
    }

    @Test
    @DisplayName("사용자의 계약이 아니면 예외 발생")
    void execute_ThrowsException_WhenNotUserContract() {
        // given
        when(contractRepository.existsByIdAndUserId(contractId, userId)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> createProofUseCase.execute(validRequest, contractId, userId))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("계약에 대한 접근 권한이 없습니다.");
    }

    @Test
    @DisplayName("이미지가 없으면 예외 발생")
    void execute_ThrowsException_WhenNoImage() {
        // given
        ProofCreateRequest requestWithoutImage = new ProofCreateRequest(null, null, null, null);
        when(contractRepository.existsByIdAndUserId(contractId, userId)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> createProofUseCase.execute(requestWithoutImage, contractId, userId))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("최소 1장 이상의 이미지가 필요합니다.");
    }

    @Test
    @DisplayName("오늘자 인증이 이미 존재하면 예외 발생")
    void execute_ThrowsException_WhenProofAlreadyExists() {
        // given
        when(contractRepository.existsByIdAndUserId(contractId, userId)).thenReturn(true);
        when(proofRepository.existsByContractIdAndCreatedAtToday(eq(contractId), any(), any())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> createProofUseCase.execute(validRequest, contractId, userId))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("해당 날짜에 이미 인증이 존재합니다.");
    }

    @Test
    @DisplayName("계약이 존재하지 않으면 예외 발생")
    void execute_ThrowsException_WhenContractNotFound() {
        // given
        when(contractRepository.existsByIdAndUserId(contractId, userId)).thenReturn(true);
        when(proofRepository.existsByContractIdAndCreatedAtToday(eq(contractId), any(), any())).thenReturn(false);
        when(contractRepository.findById(contractId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> createProofUseCase.execute(validRequest, contractId, userId))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("존재하지 않는 계약입니다.");
    }

    @Test
    @DisplayName("세 번째 이미지까지 포함한 인증 생성 성공")
    void execute_Success_WithThirdImage() {
        // given
        ProofCreateRequest requestWithThirdImage = new ProofCreateRequest("image1.jpg", "image2.jpg", "image3.jpg","테스트 코멘트");
        when(contractRepository.existsByIdAndUserId(contractId, userId)).thenReturn(true);
        when(proofRepository.existsByContractIdAndCreatedAtToday(eq(contractId), any(), any())).thenReturn(false);
        when(contractRepository.findById(contractId)).thenReturn(Optional.of(contract));
        when(proofRepository.save(any(Proof.class))).thenReturn(ProofTestUtil.createProof(1L, null, ProofStatus.APPROVED, contractId, null, LocalDateTime.now()));
        when(proofImageRepository.save(any(ProofImage.class))).thenReturn(ProofTestUtil.createProofImage());

        // when
        createProofUseCase.execute(requestWithThirdImage, contractId, userId);

        // then
        verify(proofRepository).save(any(Proof.class));
        verify(proofImageRepository, times(3)).save(any(ProofImage.class));
    }

    @Test
    @DisplayName("첫 번째 이미지만 있는 인증 생성 성공")
    void execute_Success_WithOnlyFirstImage() {
        // given
        ProofCreateRequest requestWithOnlyFirstImage = new ProofCreateRequest("image1.jpg", null, null, "테스트 코멘트");
        when(contractRepository.existsByIdAndUserId(contractId, userId)).thenReturn(true);
        when(proofRepository.existsByContractIdAndCreatedAtToday(eq(contractId), any(), any())).thenReturn(false);
        when(contractRepository.findById(contractId)).thenReturn(Optional.of(contract));
        when(proofRepository.save(any(Proof.class))).thenReturn(ProofTestUtil.createProof(1L, null, ProofStatus.APPROVED, contractId, null, LocalDateTime.now()));
        when(proofImageRepository.save(any(ProofImage.class))).thenReturn(ProofTestUtil.createProofImage());

        // when
        createProofUseCase.execute(requestWithOnlyFirstImage, contractId, userId);

        // then
        verify(proofRepository).save(any(Proof.class));
        verify(proofImageRepository, times(1)).save(any(ProofImage.class));
    }
} 