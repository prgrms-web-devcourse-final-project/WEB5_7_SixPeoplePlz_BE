package me.jinjjahalgae.domain.auth.usecase.logout;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.auth.Auth;
import me.jinjjahalgae.domain.auth.AuthRepository;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Auth 테이블의 refreshToken을 null로 바꿉니다.
 * 추후 블랙리스트, 레디스 도입 계획있음
 */
@Service
@RequiredArgsConstructor
public class LogoutUseCaseImpl implements LogoutUseCase {
    private final AuthRepository authRepository;

    @Override
    @Transactional
    public void execute(Long userId) {
        Auth auth = authRepository.findByUserId(userId)
                .orElseThrow(() -> ErrorCode.USER_NOT_FOUND.domainException("가입되지 않은 유저가 로그아웃을 시도했음"));

        auth.updateRefreshToken(null);
    }
} 