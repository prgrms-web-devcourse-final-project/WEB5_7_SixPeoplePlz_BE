package me.jinjjahalgae.domain.signature.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.signature.enums.Role;
import me.jinjjahalgae.domain.signature.enums.Validate;
import me.jinjjahalgae.domain.user.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Signature {

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

    @Enumerated(EnumType.STRING)
    private Validate validate;

    @Builder
    private Signature(Contract contract, User user, String imageKey, Role role, Validate validate) {
        this.contract = contract;
        this.user = user;
        this.imageKey = imageKey;
        this.role = role;
        this.validate = validate;
    }

    public static Signature createSignature(Contract contract, User user, String imageKey, Role role, Validate validate) {
        return Signature.builder()
                .contract(contract)
                .user(user)
                .imageKey(imageKey)
                .role(role)
                .validate(validate)
                .build();
    }
}