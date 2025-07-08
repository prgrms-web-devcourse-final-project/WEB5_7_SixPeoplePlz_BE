package me.jinjjahalgae.domain.contract.repository;

import me.jinjjahalgae.domain.contract.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepository extends JpaRepository<Contract, Long> {
}
