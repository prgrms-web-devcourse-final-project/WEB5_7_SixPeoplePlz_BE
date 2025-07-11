package me.jinjjahalgae.domain.proof.usecase.createreproof;

import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.proof.entities.Proof;
import me.jinjjahalgae.domain.proof.entities.ProofImage;
import me.jinjjahalgae.domain.proof.enums.ProofStatus;
import me.jinjjahalgae.domain.proof.repository.ProofImageRepository;
import me.jinjjahalgae.domain.proof.repository.ProofRepository;
import me.jinjjahalgae.domain.proof.usecase.create.common.ProofCreateRequest;
import me.jinjjahalgae.domain.proof.usecase.create.reproof.CreateReProofUseCaseImpl;
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
@Import(CreateReProofUseCaseImpl.class)
@Transactional
@ActiveProfiles("test")
class CreateReProofUseCaseSliceTest {

    @Autowired
    CreateReProofUseCaseImpl createReProofUseCase;

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
    Proof existingProof;
    Contract contract;
    Long proofId;
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

        existingProof = ProofTestUtil.createProofBeforeSave("원본 인증", ProofStatus.REJECTED, contractId, null);
        existingProof = proofRepository.save(existingProof);
        proofId = existingProof.getId();
    }

    @Test
    @DisplayName("재인증 생성 성공 - 슬라이스 테스트")
    void execute_Success() {
        // when
        contract.start(3);
        createReProofUseCase.execute(validRequest, proofId, userId);

        // then
        List<Proof> proofs = proofRepository.findAll();
        assertThat(proofs).hasSize(2); // 원본 인증 + 재인증

        Proof reProof = proofs.stream()
                .filter(p -> p.getProofId() != null)
                .findFirst()
                .orElse(null);
        
        assertThat(reProof).isNotNull();
        assertThat(reProof.getContractId()).isEqualTo(contractId);
        assertThat(reProof.getProofId()).isEqualTo(proofId);
        assertThat(reProof.getComment()).isEqualTo("테스트 코멘트");

        List<ProofImage> proofImages = proofImageRepository.findAll();
        assertThat(proofImages).hasSize(2);
    }

    @Test
    @DisplayName("이미지가 없으면 예외 발생 - 슬라이스 테스트")
    void execute_ThrowsException_WhenNoImage() {
        // given
        contract.start(3);
        ProofCreateRequest requestWithoutImage = new ProofCreateRequest(null, null, null, "코멘트");

        // when & then
        assertThatThrownBy(() -> createReProofUseCase.execute(requestWithoutImage, proofId, userId))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("최소 1장 이상의 이미지가 필요합니다.");
    }

    @Test
    @DisplayName("원본 인증이 존재하지 않으면 예외 발생 - 슬라이스 테스트")
    void execute_ThrowsException_WhenProofNotFound() {
        // given
        contract.start(3);
        Long nonExistentProofId = 999L;

        // when & then
        assertThatThrownBy(() -> createReProofUseCase.execute(validRequest, nonExistentProofId, userId))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("해당 인증을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("계약이 PENDING 상태일 경우 예외 발생 - 슬라이스 테스트")
    void execute_ThrowsException_ContractPending() {
        // given
        ProofCreateRequest requestWithoutImage = new ProofCreateRequest("image1.jpg", null, null, null);

        // when & then
        assertThatThrownBy(() -> createReProofUseCase.execute(requestWithoutImage, contractId, userId))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("계약 시작 전에는 인증을 생성할 수 없습니다.");
    }

    @Test
    @DisplayName("오늘자 재인증이 이미 존재하면 예외 발생 - 슬라이스 테스트")
    void execute_ThrowsException_WhenReProofAlreadyExists() {
        // given
        contract.start(3);
        Proof existingReProof = ProofTestUtil.createProofBeforeSave("기존 재인증", ProofStatus.APPROVE_PENDING, contractId, proofId);
        proofRepository.save(existingReProof);

        // when & then
        assertThatThrownBy(() -> createReProofUseCase.execute(validRequest, proofId, userId))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("해당 날짜에 이미 재인증이 존재합니다.");
    }

    @Test
    @DisplayName("세 번째 이미지까지 포함한 재인증 생성 성공 - 슬라이스 테스트")
    void execute_Success_WithThirdImage() {
        // given
        contract.start(3);
        ProofCreateRequest requestWithThirdImage = new ProofCreateRequest("image1.jpg", "image2.jpg", "image3.jpg", "테스트 코멘트");

        // when
        createReProofUseCase.execute(requestWithThirdImage, proofId, userId);

        // then
        List<Proof> proofs = proofRepository.findAll();
        assertThat(proofs).hasSize(2); // 원본 인증 + 재인증

        List<ProofImage> proofImages = proofImageRepository.findAll();
        assertThat(proofImages).hasSize(3);
    }

    @Test
    @DisplayName("계약이 존재하지 않으면 예외 발생 - 슬라이스 테스트")
    void execute_ThrowsException_WhenContractNotFound() {
        // given
        contract.start(3);
        Proof proofWithNonExistentContract = ProofTestUtil.createProofBeforeSave("테스트", ProofStatus.REJECTED, 999L, null);
        proofWithNonExistentContract = proofRepository.save(proofWithNonExistentContract);
        Long id = proofWithNonExistentContract.getId();

        // when & then
        assertThatThrownBy(() -> createReProofUseCase.execute(validRequest, id, userId))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("존재하지 않는 계약입니다.");
    }
} 