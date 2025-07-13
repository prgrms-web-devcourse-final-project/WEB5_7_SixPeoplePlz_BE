package me.jinjjahalgae.domain.proof.usecase.getcontractorprooflist;

import jakarta.persistence.EntityManager;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.proof.entities.Proof;
import me.jinjjahalgae.domain.proof.entities.ProofImage;
import me.jinjjahalgae.domain.proof.enums.ProofStatus;
import me.jinjjahalgae.domain.proof.repository.ProofImageRepository;
import me.jinjjahalgae.domain.proof.repository.ProofRepository;
import me.jinjjahalgae.domain.proof.usecase.getlist.contractorlist.dto.ContractorProofListResponse;
import me.jinjjahalgae.domain.proof.usecase.getlist.contractorlist.GetContractorProofListUseCaseImpl;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(GetContractorProofListUseCaseImpl.class)
@Transactional
@ActiveProfiles("test")
class GetContractorProofListUseCaseSliceTest {

    @Autowired
    GetContractorProofListUseCaseImpl getContractorProofListUseCase;

    @Autowired
    ProofRepository proofRepository;

    @Autowired
    ContractRepository contractRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProofImageRepository proofImageRepository;

    @Autowired
    EntityManager entityManager;

    Long contractId;
    Long userId;
    int year = 2025;
    int month = 7;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .name("test")
                .nickname("testNickname")
                .build();

        User savedUser = userRepository.save(user);
        userId = savedUser.getId();

        Contract contract = ProofTestUtil.createContractBeforeSave(3, savedUser);
        contract = contractRepository.save(contract);
        contractId = contract.getId();
    }

    @Test
    @DisplayName("계약자 인증 목록 조회 성공 - 슬라이스 테스트")
    void execute_Success() {
        // given
        // 인증 1
        Proof proof1 = ProofTestUtil.createProofBeforeSave("인증1", ProofStatus.REJECTED, contractId, null);
        Proof savedProof1 = proofRepository.save(proof1);

        ProofImage image1 = ProofTestUtil.createProofImageBeforeSave(savedProof1, 1);
        ProofImage savedImage1 = proofImageRepository.save(image1);

        ProofImage image2 = ProofTestUtil.createProofImageBeforeSave(savedProof1, 2);
        ProofImage savedImage2 = proofImageRepository.save(image2);

        savedProof1.addProofImage(savedImage1);
        savedProof1.addProofImage(savedImage2);

        // 인증 2
        Proof proof2 = ProofTestUtil.createProofBeforeSave("인증2", ProofStatus.APPROVED, contractId, null);
        Proof savedProof2 = proofRepository.save(proof2);
        ProofImage image3 = ProofTestUtil.createProofImageBeforeSave(savedProof2, 1);
        ProofImage savedImage3 = proofImageRepository.save(image3);

        savedProof2.addProofImage(savedImage3);

        // 재인증 1
        Proof reProof1 = ProofTestUtil.createProofBeforeSave("재인증1", ProofStatus.APPROVED, contractId, proof1.getId());
        Proof savedReProof = proofRepository.save(reProof1);
        ProofImage image4 = ProofTestUtil.createProofImageBeforeSave(savedProof2, 1);
        ProofImage savedImage4 = proofImageRepository.save(image4);

        savedReProof.addProofImage(savedImage4);

        // when
        List<ContractorProofListResponse> result = getContractorProofListUseCase.execute(contractId, year, month, userId);

        // then
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("사용자의 계약이 아니면 예외 발생 - 슬라이스 테스트")
    void execute_ThrowsException_WhenNotUserContract() {
        // given
        Long nonUserContractId = 999L;

        // when & then
        assertThatThrownBy(() -> getContractorProofListUseCase.execute(nonUserContractId, year, month, userId))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("계약에 대한 접근 권한이 없습니다.");
    }

    @Test
    @DisplayName("인증이 없을 때 빈 리스트 반환 - 슬라이스 테스트")
    void execute_ReturnsEmptyList_WhenNoProofs() {
        // when
        List<ContractorProofListResponse> result = getContractorProofListUseCase.execute(contractId, year, month, userId);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("재인증이 없는 인증 목록 조회 성공 - 슬라이스 테스트")
    void execute_Success_WithoutReProofs() {
        // given
        // 인증 1
        Proof proof1 = ProofTestUtil.createProofBeforeSave("인증1", ProofStatus.APPROVED, contractId, null);
        Proof savedProof1 = proofRepository.save(proof1);

        ProofImage image1 = ProofTestUtil.createProofImageBeforeSave(savedProof1, 1);
        ProofImage savedImage1 = proofImageRepository.save(image1);

        savedProof1.addProofImage(savedImage1);

        // 인증 2
        Proof proof2 = ProofTestUtil.createProofBeforeSave("인증2", ProofStatus.APPROVED, contractId, null);
        Proof savedProof2 = proofRepository.save(proof2);
        ProofImage image2 = ProofTestUtil.createProofImageBeforeSave(savedProof2, 1);
        ProofImage savedImage2 = proofImageRepository.save(image2);

        savedProof2.addProofImage(savedImage2);


        // when
        List<ContractorProofListResponse> result = getContractorProofListUseCase.execute(contractId, year, month, userId);

        // then
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("다른 달의 인증은 포함되지 않음 - 슬라이스 테스트")
    void execute_ExcludesProofsFromOtherMonths() {
        // given
        // 이번 달 인증
        Proof proofThisMonth = ProofTestUtil.createProofBeforeSave("이번 달 인증", ProofStatus.APPROVED, contractId, null);
        Proof savedThisMonth = proofRepository.save(proofThisMonth);
        ProofImage image1 = ProofTestUtil.createProofImageBeforeSave(savedThisMonth, 1);
        ProofImage savedImage1 = proofImageRepository.save(image1);

        savedThisMonth.addProofImage(savedImage1);

        // 다른 달 인증
        Proof proofOtherMonth = ProofTestUtil.createProofBeforeSave("다른 달 인증", ProofStatus.APPROVED, contractId, null);
        Proof savedOtherMonth = proofRepository.save(proofOtherMonth);

        // 리플렉션의 setField 가 DB에 반영이 되지 않아 직접 업데이트
        entityManager.createNativeQuery("UPDATE proof SET created_at = :newCreatedAt WHERE id = :id")
                .setParameter("newCreatedAt", LocalDateTime.of(2025, 6, 10, 0, 0))
                .setParameter("id", savedOtherMonth.getId())
                .executeUpdate();

        entityManager.flush();
        entityManager.clear();

        Proof newProof = proofRepository.findById(savedOtherMonth.getId()).get();
        ProofImage image2 = ProofTestUtil.createProofImageBeforeSave(newProof, 1);
        ProofImage savedImage2 = proofImageRepository.save(image2);

        newProof.addProofImage(savedImage2);


        // when
        List<ContractorProofListResponse> result = getContractorProofListUseCase.execute(contractId, year, month, userId);

        // then
        assertThat(result).hasSize(1);
    }
} 