package me.jinjjahalgae.domain.proof.repository;

import me.jinjjahalgae.domain.proof.entities.Proof;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
SELECT COUNT(p) > 0
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

    /**
     * 해당 계약에 오늘자 재인증이 존재하는 지 검사하는 쿼리
     * 시작일 이상 종료일 미만으로 오늘에 포함되는 인증만 검색
     * 오늘자 인증이 존재하면 true
     * 존재하지 않으면 false
     * @param contractId 계약 id
     * @param startOfDay 시작일(오늘)
     * @param endOfDay 종료일(내일)
     * @return boolean
     */
    @Query("""
SELECT COUNT(p) > 0
FROM Proof p
WHERE p.contractId = :contractId
AND p.createdAt >= :startOfDay
AND p.createdAt < :endOfDay
AND p.proofId IS NOT NULL
""")
    boolean existsReProofByContractIdAndCreatedAtToday(
            @Param("contractId") Long contractId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );

    /**
     * Proof 엔티티의 oneToMany 이미지 리스트 사용을 위한 fetch join
     * 인증에 무조건 하나 이상의 이미지가 존재하므로 Left join 대신 join 만 사용
     */
    @Query("SELECT p FROM Proof p JOIN FETCH p.proofImages WHERE p.id = :id")
    Optional<Proof> findByIdWithProofImages(@Param("id") Long id);

    /**
     * 최근 인증을 가져오기 위해 최근 인증의 아이디만 먼저 가져오는 쿼리
     * fetch join + limit를 같이 사용할 때 하이버네이트가 메모리에서 limit을 적용할 수 있다고 함
     * 소량의 데이터라 문제는 없어보이지만 쿼리를 분리
     * @param contractId 계약 id
     * @param pageable   페이징 (3개의 인증을 가져옴(
     * @return List<Long> 0~3개의 인증을 가져옴
     */
    @Query("SELECT p.id FROM Proof p WHERE p.contractId = :contractId ORDER BY p.id DESC")
    List<Long> findProofIdsByContractId(@Param("contractId") Long contractId, Pageable pageable);

    /**
     * 찾아온 3개의 인증 id로 인증 이미지와 fetch join
     * @param ids 인증 id들
     * @return {@link Proof}
     */
    @Query("SELECT p FROM Proof p LEFT JOIN FETCH p.proofImages WHERE p.id IN :ids")
    List<Proof> findProofsWithProofImagesByIds(@Param("ids") List<Long> ids);
}
