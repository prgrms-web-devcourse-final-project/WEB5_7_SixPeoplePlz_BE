package me.jinjjahalgae.domain.contract.repository;

import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import me.jinjjahalgae.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    Optional<Contract> findByUuid(String uuid);
    Optional<Contract> findContractById(Long contractId);

    //해당 유저의 계약 생태로 내림차순 조회
    Page<Contract> findByUserIdAndStatusInOrderByIdDesc(Long userId, List<ContractStatus> status, Pageable pageable);
    //사용자별 계약 상세 조회
    Optional<Contract> findByIdAndUserIdWithParticipation(Long contractId, Long userId);
}