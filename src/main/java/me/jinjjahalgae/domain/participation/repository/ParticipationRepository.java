package me.jinjjahalgae.domain.participation.repository;

import me.jinjjahalgae.domain.participation.entity.Participation;
import me.jinjjahalgae.domain.participation.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    // contract id가 일치하고 valid가 true인 모든 participation 객체를 리스트로 조회
    List<Participation> findByContract_Id(Long contractId);

    // 해당 유저가 계약의 valid한 참여자로 존재하는지 여부
    boolean existsByContractIdAndUserIdAndValidIsTrue(Long contractId, Long userId);

    // 유저 id와 검색을 원하는 Role로 유효한 계약 id 검색
    List<Long> findContractIdsByUserIdAndRoleAndValidIsTrue(Long userId, Role role);


}
