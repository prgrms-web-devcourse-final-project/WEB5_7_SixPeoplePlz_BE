package me.jinjjahalgae.domain.signature.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import me.jinjjahalgae.domain.signature.entity.Signature;

public interface SignatureRepository extends JpaRepository<Signature, Long> {
}
