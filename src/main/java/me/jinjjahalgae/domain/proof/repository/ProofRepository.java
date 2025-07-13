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

    @Query("SELECT p FROM Proof p LEFT JOIN FETCH p.feedbacks WHERE p.id = :id")
    Optional<Proof> findByIdWithFeedbacks(@Param("id") Long id);

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
     * 찾아온 인증 id로 인증 이미지와 fetch join
     * @param ids 인증 id들
     * @return {@link Proof}
     */
    @Query("SELECT p FROM Proof p JOIN FETCH p.proofImages WHERE p.id IN :ids")
    List<Proof> findProofsWithProofImagesByIds(@Param("ids") List<Long> ids);


    @Query("""
SELECT p.id
FROM Proof p
WHERE p.contractId = :contractId
AND p.status = 'APPROVE_PENDING'
AND NOT EXISTS (
SELECT 1
FROM Feedback f
WHERE f.proof = p
AND f.userId = :userId)""")
    List<Long> findPendingProofIdsWithoutUserFeedback(@Param("contractId") Long contractId, @Param("userId") Long userId);

    /**
     * 계약자용 해당 달의 모든 원본 인증 id를 가져오는 쿼리
     * @param contractId 계약 id
     * @param startDate 시작일 (월 기준 첫날 00:00:00)
     * @param endDate 종료일 (월 기준 마지막 날 23:59:59:999999999)
     * @return Long
     */
    @Query("""
SELECT p.id
FROM Proof p
WHERE p.contractId = :contractId
AND p.createdAt >= :startDate
AND p.createdAt <= :endDate
AND p.proofId IS NULL
ORDER BY p.createdAt ASC
""")
    List<Long> findOriginalProofIdsByMonth(@Param("contractId")Long contractId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * 계약자용 해당 달의 모든 재인증 id를 가져오는 쿼리
     * @param contractId 계약 id
     * @param startDate 시작일 (월 기준 첫날 00:00:00)
     * @param endDate 종료일 (월 기준 마지막 날 23:59:59:999999999)
     * @return Long
     */
    @Query("""
SELECT p.id
FROM Proof p
WHERE p.contractId = :contractId
AND p.createdAt >= :startDate
AND p.createdAt <= :endDate
AND p.proofId IS NOT NULL
""")
    List<Long> findReProofIdsByMonth(@Param("contractId")Long contractId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * 감독자용 해당 달의 모든 원본 인증 id를 가져오는 쿼리
     * @param contractId 계약 id
     * @param startDate 시작일 (월 기준 첫날 00:00:00)
     * @param endDate 종료일 (월 기준 마지막 날 23:59:59:999999999)
     * @return Long
     */
    @Query("""
SELECT p.id
FROM Proof p
WHERE p.contractId = :contractId
AND p.createdAt >= :startDate
AND p.createdAt <= :endDate
AND p.proofId IS NULL
AND EXISTS (
SELECT 1
FROM Feedback f
WHERE f.proof = p
AND f.userId = :userId
)
ORDER BY p.createdAt ASC
""")
    List<Long> findOriginalProofIdsByMonthForSupervisor(@Param("contractId")Long contractId,
                                                        @Param("startDate") LocalDateTime startDate,
                                                        @Param("endDate") LocalDateTime endDate,
                                                        @Param("userId") Long userId);

    /**
     * 감독자용 해당 달의 모든 재인증 id를 가져오는 쿼리
     * @param contractId 계약 id
     * @param startDate 시작일 (월 기준 첫날 00:00:00)
     * @param endDate 종료일 (월 기준 마지막 날 23:59:59:999999999)
     * @return Long
     */
    @Query("""
SELECT p.id
FROM Proof p
WHERE p.contractId = :contractId
AND p.createdAt >= :startDate
AND p.createdAt <= :endDate
AND p.proofId IS NOT NULL
AND EXISTS (
SELECT 1
FROM Feedback f
WHERE f.proof = p
AND f.userId = :userId
)
""")
    List<Long> findReProofIdsByMonthForSupervisor(@Param("contractId")Long contractId,
                                                  @Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate,
                                                  @Param("userId") Long userId);

}
