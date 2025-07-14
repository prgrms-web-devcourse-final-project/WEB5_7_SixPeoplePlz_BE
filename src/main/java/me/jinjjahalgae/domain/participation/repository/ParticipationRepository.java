package me.jinjjahalgae.domain.participation.repository;

import me.jinjjahalgae.domain.participation.entity.Participation;
import me.jinjjahalgae.domain.participation.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    // contract id가 일치하는 모든 participation 객체를 리스트로 조회
    List<Participation> findByContractId(Long contractId);

    // 해당 유저가 계약의 참여자인지
    boolean existsByContractIdAndUserId(Long contractId, Long userId);

    // 해당 유저가 계약의 참여자고 해당 role을 가지고 있는지
    boolean existsByContractIdAndUserIdAndRole(Long contractId, Long userId, Role role);

    // 해당 유저가 계약의 참여중이고 role을 가지고 있고 유효한 상태인지
    boolean existsByContractIdAndUserIdAndRoleAndValid(Long contractId, Long userId, Role role, boolean valid);

    // 해당 유저가 계약의 참여자고 특정 role을 가지고 있는지
    boolean existsByContractIdAndRole(Long contractId, Role role);
}
