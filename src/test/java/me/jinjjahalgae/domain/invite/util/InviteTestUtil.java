package me.jinjjahalgae.domain.invite.util;

import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractType;
import me.jinjjahalgae.domain.participation.entity.Participation;
import me.jinjjahalgae.domain.participation.enums.Role;
import me.jinjjahalgae.domain.user.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

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
        Contract contract = Contract.builder()
                .user(contractor)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .title("Test Contract " + id)
                .goal("Test Goal")
                .penalty("Test Penalty")
                .reward("Test Reward")
                .life(10)
                .proofPerWeek(3)
                .oneOff(false)
                .type(ContractType.BASIC)  // null 대신 BASIC 타입 사용
                .build();

        contract.initialize();

        ReflectionTestUtils.setField(contract, "id", id);
        ReflectionTestUtils.setField(contract, "uuid", "contract-uuid-" + id);
        return contract;
    }

    public static Participation createParticipation(Long id, Contract contract, User user, Role role) {
        Participation participation = Participation.builder()  // builder 패턴 사용
                .contract(contract)
                .user(user)
                .imageKey("image-key-" + id)
                .role(role)
                .valid(true)
                .build();

        ReflectionTestUtils.setField(participation, "id", id);
        return participation;
    }
}