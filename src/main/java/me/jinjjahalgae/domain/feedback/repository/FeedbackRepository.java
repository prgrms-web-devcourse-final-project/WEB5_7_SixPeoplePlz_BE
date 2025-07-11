package me.jinjjahalgae.domain.feedback.repository;

import me.jinjjahalgae.domain.feedback.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    @Query("""
SELECT f
FROM Feedback f
WHERE f.proof.contractId = :contractId
AND f.userId = :userId
""")
    List<Feedback> findByContractIdAndUserId(Long contractId, Long userId);

    // 인증id와 userid로 피드백 찾기 (이미 피드백을 한지 확인 여부)
    boolean existsByProofIdAndUserId(Long proofId, Long userId);

    // 인증 id로 피드백들 찾기
    List<Feedback> findByProofId(Long proofId);
}
