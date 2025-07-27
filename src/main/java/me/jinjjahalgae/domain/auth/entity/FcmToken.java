package me.jinjjahalgae.domain.auth.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.jinjjahalgae.domain.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor
public class FcmToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK

    private Long userId; // 유저 id (FK)

    @Column(nullable = false, unique = true)
    private String token; // FCM 토큰 값

    @Builder
    public FcmToken(Long userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    // 토큰 소유주 변경 (같은 기기로 다른 유저 로그인)
    public void updateUserId(Long userId) {
        this.userId = userId;
    }

    // 토큰 값이 DB 값과 같은지 비교
    public boolean isSameToken(String token) {
        return this.token.equals(token);
    }
}