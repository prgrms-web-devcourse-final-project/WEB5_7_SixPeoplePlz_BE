package me.jinjjahalgae.domain.participation.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import me.jinjjahalgae.domain.common.BaseEntity;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.participation.enums.Role;
import me.jinjjahalgae.domain.user.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Participation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //서명 id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id")
    private Contract contract;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String imageKey; //이미지 key (url)

    @Enumerated(EnumType.STRING)
    private Role role;

    private Boolean valid;

    @Builder
    private Participation(Contract contract, User user, String imageKey, Role role, Boolean valid) {
        this.contract = contract;
        this.user = user;
        this.imageKey = imageKey;
        this.role = role;
        this.valid = valid;
    }

    public static Participation createParticipation(Contract contract, User user, String imageKey, Role role, Boolean validate) {
        return Participation.builder()
                .contract(contract)
                .user(user)
                .imageKey(imageKey)
                .role(role)
                .valid(validate)
                .build();
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }
}