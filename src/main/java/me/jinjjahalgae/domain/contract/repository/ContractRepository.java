package me.jinjjahalgae.domain.contract.repository;

import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import me.jinjjahalgae.domain.participation.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    Optional<Contract> findByUuid(String uuid);
    Optional<Contract> findContractById(Long contractId);

    //해당 유저가 계약자인 계약을 계약 상태로 구분, 내림차순 조회
    //계약 테이블에서 가져오면 계약자임이 확실하므로 role을 받지 않음
    @Query("SELECT c FROM Contract c WHERE c.user.id = :userId AND c.status IN :statuses ORDER BY c.id DESC")
    Page<Contract> findByUserIdAndStatusInOrderByIdDesc(@Param("userId") Long userId, @Param("statuses") List<ContractStatus> statuses, Pageable pageable);

    //사용자별 계약 상세 조회
    Optional<Contract> findDetailsByIdAndUserId(Long contractId, Long userId);

    // 해당 유저의 계약인지 확인용
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END FROM Contract c WHERE c.id = :contractId AND c.user.id = :userId")
    boolean existsByIdAndUserId(@Param("contractId") Long contractId, @Param("userId") Long userId);

    // 계약 조회 시 관련한 유저 정보도 한번에
    @Query("SELECT c FROM Contract c JOIN FETCH c.user WHERE c.id = :contractId")
    Optional<Contract> findByIdWithUser(@Param("contractId") Long contractId);

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
}