package me.jinjjahalgae.domain.contract.repository;

import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    Optional<Contract> findByUuid(String uuid);
    Optional<Contract> findContractById(Long contractId);

    //해당 유저의 계약 상태로 내림차순 조회
    Page<Contract> findByUserIdAndStatusInOrderByIdDesc(Long userId, List<ContractStatus> status, Pageable pageable);

    //사용자별 계약 상세 조회
    Optional<Contract> findDetailsByIdAndUserId(Long contractId, Long userId);

    // 해당 유저의 계약인지 확인용
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END FROM Contract c WHERE c.id = :contractId AND c.user.id = :userId")
    boolean existsByIdAndUserId(@Param("contractId") Long contractId, @Param("userId") Long userId);

    // PENDING 상태 계약 확인용
    boolean existsByIdAndStatus(Long id, ContractStatus status);

    // 시작일로 대기중 계약 조회
    @Query("SELECT c FROM Contract c WHERE c.status = :status AND FUNCTION('DATE', c.startDate) = :date AND c.oneOff = :oneOff")
    List<Contract> findByStatusAndStartDateOnAndOneOff(@Param("status") ContractStatus status, @Param("date") LocalDate date, @Param("oneOff") boolean oneOff);

    // 종료일로 진행중 계약 조회
    @Query("SELECT c FROM Contract c WHERE c.status = :status AND FUNCTION('DATE', c.endDate) = :date AND c.oneOff = :oneOff")
    List<Contract> findByStatusAndEndDateOnAndOneOff(@Param("status") ContractStatus status, @Param("date") LocalDate date, @Param("oneOff") boolean oneOff);

    // 계약 조회 시 관련한 유저 정보도 한번에
    @Query("SELECT c FROM Contract c JOIN FETCH c.user WHERE c.id = :contractId")
    Optional<Contract> findByIdWithUser(@Param("contractId") Long contractId);

    // 생성된 지 24시간이 지났고 서명한 감독자가 한 명도 없고 PENDING 상태의 단건 계약 목록 조회
    @Query("""
        select c
        from Contract c
        join fetch c.user
        where c.oneOff = true
            and c.status = 'PENDING'
            and c.createdAt <= :date
            and not exists
                (select p
                from Participation p
                where p.contract = c and p.role = 'SUPERVISOR')
    """)
    List<Contract> findExpiredOneOffContractsWithNoSupervisors(@Param("date") LocalDateTime date);

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
    List<Contract> findCompletableOneOffContracts(@Param("date") LocalDateTime date);

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
    List<Contract> findFailableOneOffContracts(@Param("date") LocalDateTime date);
}