package me.jinjjahalgae.domain.proof.repository;

import me.jinjjahalgae.domain.proof.entities.Proof;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProofRepository extends JpaRepository<Proof, Long> {
}
