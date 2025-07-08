package me.jinjjahalgae.domain.proof.repository;

import me.jinjjahalgae.domain.proof.entities.ProofImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProofImageRepository extends JpaRepository<ProofImage, Long> {
}
