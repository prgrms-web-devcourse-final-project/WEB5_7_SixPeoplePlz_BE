package me.jinjjahalgae.domain.signature.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import me.jinjjahalgae.domain.contract.entity.Contract;
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

    @Builder
    private Signature(Contract contract, User user, String imageKey) {
        this.contract = contract;
        this.user = user;
        this.imageKey = imageKey;
    }

    public static Signature of(Contract contract, User user, String imageKey) {
        return Signature.builder()
                .contract(contract)
                .user(user)
                .imageKey(imageKey)
                .build();
    }
}