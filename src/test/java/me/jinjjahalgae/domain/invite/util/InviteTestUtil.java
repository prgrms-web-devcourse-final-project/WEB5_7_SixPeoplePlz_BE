package me.jinjjahalgae.domain.invite.util;

import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.participation.entity.Participation;
import me.jinjjahalgae.domain.participation.enums.Role;
import me.jinjjahalgae.domain.user.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.UUID;

public class InviteTestUtil {

    public static User createUser(Long id, String name, String nickname) {
        return User.builder()
                .id(id)
                .name(name)
                .nickname(nickname)
                .email(name + "@test.com")
                .build();
    }

    public static Contract createContract(Long id, User contractor) {
        Contract contract = Contract.createContract(
                contractor,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30),
                "Test Contract " + id,
                "Test Goal",
                "Test Penalty",
                "Test Reward",
                10,
                3,
                false,
                null // ContractType
        );
        // 테스트 편의를 위해 ID와 UUID를 강제로 설정
        // 실제로는 JPA가 ID를, createContract 내부 로직이 UUID를 생성
        ReflectionTestUtils.setField(contract, "id", id);
        ReflectionTestUtils.setField(contract, "uuid", "contract-uuid-" + id);
        return contract;
    }

    public static Participation createParticipation(Long id, Contract contract, User user, Role role) {
        Participation participation = Participation.createParticipation(
                contract,
                user,
                "image-key-" + id,
                role,
                true
        );
        ReflectionTestUtils.setField(participation, "id", id);
        return participation;
    }
}