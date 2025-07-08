package me.jinjjahalgae.domain.proof.repository;

import me.jinjjahalgae.domain.proof.entities.Proof;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ProofRepository extends JpaRepository<Proof, Long> {

    /**
     * 해당 계약에 오늘자 인증이 존재하는 지 검사하는 쿼리
     * 시작일 이상 종료일 미만으로 오늘에 포함되는 인증만 검색
     * 오늘자 인증이 존재하면 true
     * 존재하지 않으면 false
     * @param contractId 계약 id
     * @param startOfDay 시작일(오늘)
     * @param endOfDay 종료일(내일)
     * @return boolean
     */
    @Query("""
SELECT COUNT(P) > 0
FROM Proof p
WHERE p.contractId = :contractId
AND p.createdAt >= :startOfDay
AND p.createdAt < :endOfDay
""")
    boolean existsByContractIdAndCreatedAtToday(
            @Param("contractId") Long contractId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );
}
