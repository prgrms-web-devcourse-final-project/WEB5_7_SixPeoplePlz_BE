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
}
