package me.jinjjahalgae.domain.auth;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.jinjjahalgae.domain.auth.enums.Provider;
import me.jinjjahalgae.domain.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor
public class Auth extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // auth id

    @Enumerated(EnumType.STRING)
    private Provider provider; // 써드파티 제공자

    private String oauthId; // 써드파티 식별자

    private String refreshToken; // 리프레시 토큰

    private Long userId; // 유저 id (FK)

    @Builder
    public Auth(Long id, Provider provider, String oauthId, String refreshToken, Long userId) {
        this.id = id;
        this.provider = provider;
        this.oauthId = oauthId;
        this.refreshToken = refreshToken;
        this.userId = userId;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
