package me.jinjjahalgae.domain.contract.repository;

import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import me.jinjjahalgae.domain.participation.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    Optional<Contract> findByUuid(String uuid);
    Optional<Contract> findContractById(Long contractId);

    List<Contract> findByStatus(ContractStatus status);

    //해당 유저가 계약자인 계약을 계약 상태로 구분, 내림차순 조회
    //계약 테이블에서 가져오면 계약자임이 확실하므로 role을 받지 않음
    @Query("SELECT c FROM Contract c WHERE c.user.id = :userId AND c.status IN :statuses ORDER BY c.id DESC")
    Page<Contract> findByUserIdAndStatusInOrderByIdDesc(@Param("userId") Long userId, @Param("statuses") List<ContractStatus> statuses, Pageable pageable);

    //사용자별 계약 상세 조회
    Optional<Contract> findDetailsByIdAndUserId(Long contractId, Long userId);

    // 해당 유저의 계약인지 확인용
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END FROM Contract c WHERE c.id = :contractId AND c.user.id = :userId")
    boolean existsByIdAndUserId(@Param("contractId") Long contractId, @Param("userId") Long userId);

    // IN_PROGRESS 상태 계약 확인용
    boolean existsByIdAndStatus(Long id, ContractStatus status);

    // 시작일로 대기중 계약 조회
    @Query("SELECT c FROM Contract c WHERE c.status = :status AND c.startDate = :date AND c.oneOff = :oneOff")
    List<Contract> findByStatusAndStartDateOnAndOneOff(@Param("status") ContractStatus status, @Param("date") Instant date, @Param("oneOff") boolean oneOff);

    // 종료일로 진행중 계약 조회
    @Query("SELECT c FROM Contract c WHERE c.status = :status AND c.endDate = :date AND c.oneOff = :oneOff")
    List<Contract> findByStatusAndEndDateOnAndOneOff(@Param("status") ContractStatus status, @Param("date") Instant date, @Param("oneOff") boolean oneOff);

    // 시작일로 대기중인 일반(단발이 아닌) 계약 조회
    @Query("SELECT c FROM Contract c WHERE c.status = :status AND c.startDate = :date AND c.oneOff = false")
    List<Contract> findByStatusAndStartDateOn(@Param("status") ContractStatus status, @Param("date") Instant date);

    // 계약 조회 시 관련한 유저 정보도 한번에
    @Query("SELECT c FROM Contract c JOIN FETCH c.user WHERE c.id = :contractId")
    Optional<Contract> findByIdWithUser(@Param("contractId") Long contractId);

    // 생성된 지 24시간이 지났고 서명한 감독자가 한 명도 없고 PENDING 상태의 단건 계약 목록 조회
    @Query("""
        select c
        from Contract c
        join fetch c.user
        where c.oneOff = true
            and c.status IN ('PENDING', 'IN_PROGRESS')
            and c.createdAt <= :date
            and not exists
                (select p
                from Participation p
                where p.contract = c and p.role = 'SUPERVISOR')
    """)
    List<Contract> findExpiredOneOffContractsWithNoSupervisors(@Param("date") Instant date);

    // 시작된 지 24시간이 지났고 인증이 하나 이상 존재하고 피드백이 하나라도 없는 진행중인 단건 계약 목록 조회
    @Query("""
        select c from Contract c
        join fetch c.user
        where c.oneOff = true
          and c.status = 'IN_PROGRESS'
          and c.startDate <= :date
          and exists
              (select 1
              from Proof p
              where p.contractId = c.id)
          and not exists
              (select 1
                from Feedback f
                where f.proof.id in (
                    select p.id
                    from Proof p
                    where p.contractId = c.id))
    """)
    List<Contract> findCompletableOneOffContracts(@Param("date") Instant date);

    // 시작된 지 24시간이 지났고 인증이 하나도 없고 진행중인 단건 계약 목록 조회
    @Query("""
        select c from Contract c
        join fetch c.user
        where c.oneOff = true
          and c.status = 'IN_PROGRESS'
          and c.startDate <= :date
          and not exists
              (select 1
              from Proof p
              where p.contractId = c.id)
    """)
    List<Contract> findFailableOneOffContracts(@Param("date") Instant date);
    // 여러 계약의 상태를 한 번에 업데이트하는 벌크 쿼리
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Contract c SET c.status = :status WHERE c.id IN :ids")
    void bulkUpdateStatus(@Param("ids") List<Long> ids, @Param("status") ContractStatus status);

    /**
     * 종료일이 오늘이고 처리할 인증이 "없는" 일반(단발이 아닌) 계약을 WAIT_RESULT(결과 대기)로 변경
     * @param today 날짜
     */
    @Modifying(clearAutomatically = true)
    @Query("""
UPDATE Contract c
SET c.status = 'WAIT_RESULT'
WHERE c.status = 'IN_PROGRESS' AND c.endDate <= :today
AND c.oneOff = false
AND NOT EXISTS (
    SELECT p FROM Proof p
    WHERE p.contractId = c.id AND p.status = 'APPROVE_PENDING'
)
""")
    void bulkUpdateCompletedContractsToWait(@Param("today") Instant today);

    /**
     * 종료일이 오늘이고 처리할 인증이 "있는" 일반(단발이 아닌) 계약을 WAIT_RESULT(결과 대기)로 변경
     * @param today 날짜
     */
    @Modifying(clearAutomatically = true)
    @Query("""
UPDATE Contract c
SET c.status = 'WAIT_RESULT'
WHERE c.status = 'IN_PROGRESS' AND c.endDate <= :today
AND c.oneOff = false
AND EXISTS (
    SELECT p FROM Proof p
    WHERE p.contractId = c.id AND p.status = 'APPROVE_PENDING'
)
""")
    void bulkUpdateApprovePendingContractsToWait(@Param("today") Instant today);

    // 감독자로 참여한 계약 조회 (대기, 진행만)
    @Query("SELECT c FROM Contract c " +
            "JOIN c.participations p " +
            "WHERE p.user.id = :userId AND p.role = :role AND p.valid = true " +
            "AND c.status IN :statuses ORDER BY c.id DESC")
    Page<Contract> findContractByParticipantUserIdAndRoleAndStatusInOrderByIdDesc(
            @Param("userId") Long userId,
            @Param("role") Role role,
            @Param("statuses") List<ContractStatus> statuses,
            Pageable pageable
    );

    //해당 계약에서 유효한 참가자인가??
    @Query("SELECT c FROM Contract c " +
            "JOIN c.participations p " +
            "WHERE c.id = :contractId AND p.user.id = :userId AND p.valid = true")
    Optional<Contract> findValidParticipantByIdAndUserId(Long contractId, Long userId);

    // TODO: 나중에 mroonga로 리팩토링 고려해보기
    // 계약 히스토리 검색 (role, keyword, endDate, status 조건)
    @Query("""
        SELECT DISTINCT c
        FROM Contract c
        JOIN c.participations p
        WHERE p.user.id = :userId
        AND p.role = :role
        AND p.valid = true
        AND (:keyword IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
        AND (:endDate IS NULL OR c.endDate <= :endDate)
        AND (
             (:status IS NULL AND c.status IN ('COMPLETED', 'FAILED', 'ABANDONED'))
             OR
             (:status IS NOT NULL AND c.status = :status)
         )
        ORDER BY c.endDate DESC
        """)
    Page<Contract> findContractHistoryByConditions(
        @Param("userId") Long userId,
        @Param("role") Role role,
        @Param("keyword") String keyword,
        @Param("endDate") Instant endDate,
        @Param("status") ContractStatus status,
        Pageable pageable
    );

    /**
     * 어제 또는 이전에 종료되었어야 하는 '진행중' 또는 '결과 대기' 상태의 모든 계약을 조회
     * @param statuses 조회할 계약 상태 목록 (IN_PROGRESS, WAIT_RESULT)
     * @param before 기준 날짜 (어제)
     * @return 조건에 맞는 계약 목록
     */
    @Query("""
        SELECT c
        FROM Contract c
        WHERE c.status IN :statuses
        AND c.endDate <= :before
    """)
    List<Contract> findContractsToEnd(@Param("statuses") List<ContractStatus> statuses, @Param("before") Instant before);

    @Modifying
    @Query(
            value = "UPDATE contract SET current_proof = current_proof + 1 WHERE id IN (:contractIds)",
            nativeQuery = true
    )
    void incrementCurrentProofForContracts(@Param("contractIds") List<Long> contractIds);


    // 시작된 지 24시간이 지난 '진행중', '결과대기' 상태의 단발성 계약을 모두 조회
    @Query("""
        select c
        from Contract c
        where c.oneOff = true
        and c.status in ("IN_PROGRESS", "WAIT_RESULT")
        and c.startDate <= :date
    """)
    List<Contract> findEndedOneOffContracts(@Param("date") Instant date);
}