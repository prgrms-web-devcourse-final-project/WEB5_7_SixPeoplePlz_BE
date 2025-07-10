package me.jinjjahalgae.domain.auth.usecase.refresh;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.auth.Auth;
import me.jinjjahalgae.domain.auth.AuthRepository;
import me.jinjjahalgae.domain.auth.mapper.AuthMapper;
import me.jinjjahalgae.domain.auth.usecase.refresh.dto.RefreshRequest;
import me.jinjjahalgae.domain.auth.usecase.refresh.dto.RefreshResponse;
import me.jinjjahalgae.global.exception.ErrorCode;
import me.jinjjahalgae.global.security.jwt.JwtTokenProvider;
import me.jinjjahalgae.global.security.jwt.Token;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * refreshToken 검증 후, jwt 페어 재발행
 */
@Service
@RequiredArgsConstructor
public class RefreshUseCaseImpl implements RefreshUseCase {
    private final AuthRepository authRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthMapper authMapper;

    @Override
    @Transactional
    public RefreshResponse execute(RefreshRequest request) {
        String refreshToken = request.refreshToken();

        // userId를 refreshToken에서 추출
        Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);

        // userId로 auth 테이블 조회
        Auth auth = authRepository.findByUserId(userId)
                .orElseThrow(() -> ErrorCode.INVALID_TOKEN.domainException("유효하지 않은 리프레시 토큰"));

        // refreshToken과 매칭
        if (!refreshToken.equals(auth.getRefreshToken())) {
            throw ErrorCode.INVALID_TOKEN.domainException("유효하지 않은 리프레시 토큰");
        }

        // 리프레시 토큰 유효성 검사
        jwtTokenProvider.validateToken(refreshToken);

        // 토큰 페어 재발행
        Token newToken = jwtTokenProvider.generateToken(auth.getUserId());

        // 리프레시 토큰 갱신
        auth.updateRefreshToken(newToken.refreshToken());

        return authMapper.toRefreshResponse(newToken);
    }
} 