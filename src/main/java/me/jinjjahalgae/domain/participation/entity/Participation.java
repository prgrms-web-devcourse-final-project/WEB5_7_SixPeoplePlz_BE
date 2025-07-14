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

import java.util.Objects;

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

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    // 참여 여부를 false로 변경하는 메서드
    public void invalidate() {
        this.valid = false;
    }

    // 감독자인지 검증
    public boolean isSupervisor() {
        return this.role == Role.SUPERVISOR;
    }

    // 계약자인지 검증
    public boolean isContractor() {
        return this.role == Role.CONTRACTOR;
    }

    // 계약에 참가중인 유저인지 검증
    public boolean isSameUser(Long userId) {
        return Objects.equals(this.user.getId(), userId);
    }

    // 참가중인 상태인지 검증
    public boolean isValid() {
        return Boolean.TRUE.equals(this.valid);
    }
}
