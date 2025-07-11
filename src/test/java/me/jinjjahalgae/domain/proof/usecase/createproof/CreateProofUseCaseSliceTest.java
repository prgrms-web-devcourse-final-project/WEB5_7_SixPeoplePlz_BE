package me.jinjjahalgae.domain.proof.usecase.createproof;

import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.proof.entities.Proof;
import me.jinjjahalgae.domain.proof.entities.ProofImage;
import me.jinjjahalgae.domain.proof.enums.ProofStatus;
import me.jinjjahalgae.domain.proof.repository.ProofImageRepository;
import me.jinjjahalgae.domain.proof.repository.ProofRepository;
import me.jinjjahalgae.domain.proof.usecase.create.proof.CreateProofUseCaseImpl;
import me.jinjjahalgae.domain.proof.usecase.create.common.ProofCreateRequest;
import me.jinjjahalgae.domain.proof.util.ProofTestUtil;
import me.jinjjahalgae.domain.user.User;
import me.jinjjahalgae.domain.user.UserRepository;
import me.jinjjahalgae.global.exception.AppException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(CreateProofUseCaseImpl.class)
@Transactional
@ActiveProfiles("test")
class CreateProofUseCaseSliceTest {

    @Autowired
    CreateProofUseCaseImpl createProofUseCase;

    @Autowired
    ProofRepository proofRepository;

    @Autowired
    ProofImageRepository proofImageRepository;

    @Autowired
    ContractRepository contractRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TestEntityManager entityManager;

    ProofCreateRequest validRequest;
    Contract contract;
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

        validRequest = new ProofCreateRequest("image1.jpg", "image2.jpg", null, "테스트 코멘트");

        contract = ProofTestUtil.createContractBeforeSave(3, savedUser);
        contract = contractRepository.save(contract);
        contractId = contract.getId();
    }

    @Test
    @DisplayName("인증 생성 성공 - 슬라이스 테스트")
    void execute_Success() {
        // when
        createProofUseCase.execute(validRequest, contractId, userId);

        // then
        List<Proof> proofs = proofRepository.findAll();
        assertThat(proofs).hasSize(1);
        
        Proof savedProof = proofs.get(0);
        assertThat(savedProof.getContractId()).isEqualTo(contractId);
        assertThat(savedProof.getComment()).isEqualTo("테스트 코멘트");
        assertThat(savedProof.getStatus()).isEqualTo(ProofStatus.APPROVE_PENDING);

        List<ProofImage> proofImages = proofImageRepository.findAll();
        assertThat(proofImages).hasSize(2);
    }

    @Test
    @DisplayName("이미지가 없으면 예외 발생 - 슬라이스 테스트")
    void execute_ThrowsException_WhenNoImage() {
        // given
        ProofCreateRequest requestWithoutImage = new ProofCreateRequest(null, null, null, null);

        // when & then
        assertThatThrownBy(() -> createProofUseCase.execute(requestWithoutImage, contractId, userId))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("최소 1장 이상의 이미지가 필요합니다.");
    }

    @Test
    @DisplayName("오늘자 인증이 이미 존재하면 예외 발생 - 슬라이스 테스트")
    void execute_ThrowsException_WhenProofAlreadyExists() {
        // given
        Proof existingProof = ProofTestUtil.createProofBeforeSave("기존 인증", ProofStatus.APPROVE_PENDING, contractId, null);
        proofRepository.save(existingProof);

        // when & then
        assertThatThrownBy(() -> createProofUseCase.execute(validRequest, contractId, userId))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("해당 날짜에 이미 인증이 존재합니다.");
    }

    @Test
    @DisplayName("세 번째 이미지까지 포함한 인증 생성 성공 - 슬라이스 테스트")
    void execute_Success_WithThirdImage() {
        // given
        ProofCreateRequest requestWithThirdImage = new ProofCreateRequest("image1.jpg", "image2.jpg", "image3.jpg", "테스트 코멘트");

        // when
        createProofUseCase.execute(requestWithThirdImage, contractId, userId);

        // then
        List<Proof> proofs = proofRepository.findAll();
        assertThat(proofs).hasSize(1);

        List<ProofImage> proofImages = proofImageRepository.findAll();
        assertThat(proofImages).hasSize(3);
    }

    @Test
    @DisplayName("첫 번째 이미지만 있는 인증 생성 성공 - 슬라이스 테스트")
    void execute_Success_WithOnlyFirstImage() {
        // given
        ProofCreateRequest requestWithOnlyFirstImage = new ProofCreateRequest("image1.jpg", null, null, "테스트 코멘트");

        // when
        createProofUseCase.execute(requestWithOnlyFirstImage, contractId, userId);

        // then
        List<Proof> proofs = proofRepository.findAll();
        assertThat(proofs).hasSize(1);

        List<ProofImage> proofImages = proofImageRepository.findAll();
        assertThat(proofImages).hasSize(1);
    }

    @Test
    @DisplayName("계약이 존재하지 않으면 예외 발생 - 슬라이스 테스트")
    void execute_ThrowsException_WhenContractNotFound() {
        // given
        Long nonExistentContractId = 999L;

        // when & then
        assertThatThrownBy(() -> createProofUseCase.execute(validRequest, nonExistentContractId, userId))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("계약에 대한 접근 권한이 없습니다.");
    }
} 