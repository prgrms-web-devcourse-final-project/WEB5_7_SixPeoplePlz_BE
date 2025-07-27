package me.jinjjahalgae.domain.auth.repository;

import me.jinjjahalgae.domain.auth.entity.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

    // FCM 토큰 값으로 토큰 정보를 조회
    Optional<FcmToken> findByToken(String token);

    // 사용자가 가진 모든 FCM 토큰 목록을 조회
    List<FcmToken> findAllByUserId(Long userId);

    // 여러 유저 ID에 해당하는 모든 FCM 토큰을 조회
    List<FcmToken> findAllByUserIdIn(List<Long> userIds);
}
