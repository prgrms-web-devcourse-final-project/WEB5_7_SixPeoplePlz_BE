package me.jinjjahalgae.domain.signature.repository;

import me.jinjjahalgae.domain.signature.entity.Signature;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SignatureRepository extends JpaRepository<Signature, Long> {
}
