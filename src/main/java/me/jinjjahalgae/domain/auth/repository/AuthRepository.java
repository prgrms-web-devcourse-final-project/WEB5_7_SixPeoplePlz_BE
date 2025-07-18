package me.jinjjahalgae.domain.auth.repository;

import me.jinjjahalgae.domain.auth.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<Auth, Long> {
    Optional<Auth> findByOauthId(String oauthId);

    Optional<Auth> findByUserId(Long userId);

    Optional<Auth> findByRefreshToken(String refreshToken);
}
