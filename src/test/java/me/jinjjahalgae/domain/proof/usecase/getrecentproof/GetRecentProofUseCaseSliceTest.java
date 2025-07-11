package me.jinjjahalgae.domain.proof.usecase.getrecentproof;

import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.proof.entities.Proof;
import me.jinjjahalgae.domain.proof.entities.ProofImage;
import me.jinjjahalgae.domain.proof.enums.ProofStatus;
import me.jinjjahalgae.domain.proof.repository.ProofImageRepository;
import me.jinjjahalgae.domain.proof.repository.ProofRepository;
import me.jinjjahalgae.domain.proof.usecase.get.recent.GetRecentProofUseCaseImpl;
import me.jinjjahalgae.domain.proof.usecase.get.recent.dto.ProofRecentResponse;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(GetRecentProofUseCaseImpl.class)
@ActiveProfiles("test")
class GetRecentProofUseCaseSliceTest {

    @Autowired
    GetRecentProofUseCaseImpl getRecentProofUseCase;

    @Autowired
    ProofRepository proofRepository;

    @Autowired
    ContractRepository contractRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProofImageRepository proofImageRepository;

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
    }

    @Test
    @DisplayName("최근 인증 목록 조회 성공 - 슬라이스 테스트")
    void execute_Success() {
        // given
        // 인증 1
        Proof proof1 = ProofTestUtil.createProofBeforeSave("최근 인증1", ProofStatus.APPROVED, contractId, null);
        Proof save1 = proofRepository.save(proof1);
        ProofImage image1 = ProofTestUtil.createProofImageBeforeSave(save1, 1);
        ProofImage savedImage1 = proofImageRepository.save(image1);

        save1.addProofImage(savedImage1);

        // 인증 2
        Proof proof2 = ProofTestUtil.createProofBeforeSave("최근 인증2", ProofStatus.APPROVED, contractId, null);
        Proof save2 = proofRepository.save(proof2);
        ProofImage image2 = ProofTestUtil.createProofImageBeforeSave(save2, 1);
        ProofImage savedImage2 = proofImageRepository.save(image2);

        save2.addProofImage(savedImage2);

        // 인증 3
        Proof proof3 = ProofTestUtil.createProofBeforeSave("최근 인증3", ProofStatus.APPROVED, contractId, null);
        Proof save3 = proofRepository.save(proof3);
        ProofImage image3 = ProofTestUtil.createProofImageBeforeSave(save3, 1);
        ProofImage savedImage3 = proofImageRepository.save(image3);

        save3.addProofImage(savedImage3);

        // 인증 4
        Proof proof4 = ProofTestUtil.createProofBeforeSave("최근 인증4", ProofStatus.APPROVED, contractId, null);
        Proof save4 = proofRepository.save(proof4);
        ProofImage image4 = ProofTestUtil.createProofImageBeforeSave(save4, 1);
        ProofImage savedImage4 = proofImageRepository.save(image4);

        save4.addProofImage(savedImage4);

        // when
        List<ProofRecentResponse> result = getRecentProofUseCase.execute(contractId, userId);

        // then
        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("사용자의 계약이 아니면 예외 발생 - 슬라이스 테스트")
    void execute_ThrowsException_WhenNotUserContract() {
        // given
        Long nonUserContractId = 999L;

        // when & then
        assertThatThrownBy(() -> getRecentProofUseCase.execute(nonUserContractId, userId))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("계약에 대한 접근 권한이 없습니다.");
    }

    @Test
    @DisplayName("최근 인증이 없을 때 빈 리스트 반환 - 슬라이스 테스트")
    void execute_ReturnsEmptyList_WhenNoRecentProofs() {
        // when
        List<ProofRecentResponse> result = getRecentProofUseCase.execute(contractId, userId);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("최근 인증이 3개 미만일 때 성공 - 슬라이스 테스트")
    void execute_Success_WhenLessThanThreeProofs() {
        // given
        // 인증 1
        Proof proof1 = ProofTestUtil.createProofBeforeSave("최근 인증1", ProofStatus.APPROVED, contractId, null);
        Proof save1 = proofRepository.save(proof1);
        ProofImage image1 = ProofTestUtil.createProofImageBeforeSave(save1, 1);
        ProofImage savedImage1 = proofImageRepository.save(image1);

        save1.addProofImage(savedImage1);

        // 인증 2
        Proof proof2 = ProofTestUtil.createProofBeforeSave("최근 인증2", ProofStatus.APPROVED, contractId, null);
        Proof save2 = proofRepository.save(proof2);
        ProofImage image2 = ProofTestUtil.createProofImageBeforeSave(save2, 1);
        ProofImage savedImage2 = proofImageRepository.save(image2);

        save2.addProofImage(savedImage2);

        // when
        List<ProofRecentResponse> result = getRecentProofUseCase.execute(contractId, userId);

        // then
        assertThat(result).hasSize(2);
    }
} 