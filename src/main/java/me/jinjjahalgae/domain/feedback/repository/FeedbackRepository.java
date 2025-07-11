package me.jinjjahalgae.domain.feedback.repository;

import me.jinjjahalgae.domain.feedback.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    @Query("""
SELECT f
FROM Feedback f
WHERE f.proof.contractId = :contractId
AND f.userId = :userId
""")
    List<Feedback> findByContractIdAndUserId(Long contractId, Long userId);

    /**
     * 계약 id를 기준으로 참여한 유저가 SUPERVISOR고 유효할 때 feedback을 남긴 적 없는 user에 대해서
     * 자동 승인 처리 컬럼을 insert 하는 native 쿼리
     * @param proofId 인증 id
     * @param contractId 계약 id
     * @param now insert 시점
     */
    @Modifying
    @Query(value = """
INSERT INTO feedback (user_id, proof_id, status, comment, created_at, updated_at) 
SELECT p.user_id, :proofId, 'APPROVED', '자동 승인 처리', :now, :now 
FROM participation p 
WHERE p.contract_id = :contractId 
  AND p.role = 'SUPERVISOR' 
  AND p.valid = true 
  AND NOT EXISTS (SELECT 1 FROM feedback f WHERE f.proof_id = :proofId AND f.user_id = p.user_id)""",
            nativeQuery = true)
    void createAutoApprovalFeedbacks(@Param("proofId") Long proofId, @Param("contractId") Long contractId, @Param("now") LocalDateTime now);
}
