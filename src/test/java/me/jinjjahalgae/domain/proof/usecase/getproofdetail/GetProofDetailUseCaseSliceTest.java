package me.jinjjahalgae.domain.proof.usecase.getproofdetail;

import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.participation.entity.Participation;
import me.jinjjahalgae.domain.participation.repository.ParticipationRepository;
import me.jinjjahalgae.domain.proof.entities.Proof;
import me.jinjjahalgae.domain.proof.entities.ProofImage;
import me.jinjjahalgae.domain.proof.enums.ProofStatus;
import me.jinjjahalgae.domain.proof.repository.ProofImageRepository;
import me.jinjjahalgae.domain.proof.repository.ProofRepository;
import me.jinjjahalgae.domain.proof.usecase.get.detail.GetProofDetailUseCaseImpl;
import me.jinjjahalgae.domain.proof.usecase.get.detail.dto.ProofDetailResponse;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(GetProofDetailUseCaseImpl.class)
@ActiveProfiles("test")
class GetProofDetailUseCaseSliceTest {

    @Autowired
    GetProofDetailUseCaseImpl getProofDetailUseCase;

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
    TestEntityManager entityManager;

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

        Contract contract = ProofTestUtil.createContractBeforeSave( 3, savedUser);
        Contract savedContract = contractRepository.save(contract);
        contractId = savedContract.getId();

        Participation participation = ProofTestUtil.createParticipationBeforeSave(savedContract, user);
        participationRepository.save(participation);

        Proof proof = ProofTestUtil.createProofBeforeSave("테스트 인증", ProofStatus.APPROVED, contractId, null);
        Proof savedProof = proofRepository.save(proof);
        ProofImage image1 = ProofTestUtil.createProofImageBeforeSave(savedProof, 1);
        ProofImage savedImage1 = proofImageRepository.save(image1);

        savedProof.addProofImage(savedImage1);
        proofId = savedProof.getId();
    }

    @Test
    @DisplayName("인증 상세 조회 성공 - 슬라이스 테스트")
    void execute_Success() {
        // when
        ProofDetailResponse result = getProofDetailUseCase.execute(proofId, userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.comment()).isEqualTo("테스트 인증");
    }

    @Test
    @DisplayName("인증이 존재하지 않으면 예외 발생 - 슬라이스 테스트")
    void execute_ThrowsException_WhenProofNotFound() {
        // given
        Long nonExistentProofId = 999L;

        // when & then
        assertThatThrownBy(() -> getProofDetailUseCase.execute(nonExistentProofId, userId))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("해당 인증을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("사용자가 계약의 참여자가 아니면 예외 발생 - 슬라이스 테스트")
    void execute_ThrowsException_WhenNotParticipant() {
        // given
        Long nonParticipantUserId = 999L;

        // when & then
        assertThatThrownBy(() -> getProofDetailUseCase.execute(proofId, nonParticipantUserId))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("계약에 대한 접근 권한이 없습니다.");
    }
} 