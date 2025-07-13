package me.jinjjahalgae.domain.contract.repository;

import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
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
    @Query("SELECT c FROM Contract c WHERE c.status = :status AND FUNCTION('DATE', c.startDate) = :date")
    List<Contract> findByStatusAndStartDateOn(@Param("status") ContractStatus status, @Param("date") LocalDate date);

    // 종료일로 진행중 계약 조회
    @Query("SELECT c FROM Contract c WHERE c.status = :status AND FUNCTION('DATE', c.endDate) = :date")
    List<Contract> findByStatusAndEndDateOn(@Param("status") ContractStatus status, @Param("date") LocalDate date);

    // 계약 조회 시 관련한 유저 정보도 한번에
    @Query("SELECT c FROM Contract c JOIN FETCH c.user WHERE c.id = :contractId")
    Optional<Contract> findByIdWithUser(@Param("contractId") Long contractId);
}