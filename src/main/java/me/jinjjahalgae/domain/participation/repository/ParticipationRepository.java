package me.jinjjahalgae.domain.participation.repository;

import me.jinjjahalgae.domain.participation.entity.Participation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    // contract id가 일치하는 모든 participation 객체를 리스트로 조회
    List<Participation> findByContract_Id(Long contractId);
}
