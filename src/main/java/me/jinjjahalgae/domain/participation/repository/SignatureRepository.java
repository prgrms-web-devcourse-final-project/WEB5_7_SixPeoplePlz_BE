package me.jinjjahalgae.domain.participation.repository;

import me.jinjjahalgae.domain.participation.entity.Participation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SignatureRepository extends JpaRepository<Participation, Long> {
}
