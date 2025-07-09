package me.jinjjahalgae.domain.user.usecase;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.user.UserRepository;
import me.jinjjahalgae.domain.user.usecase.interfaces.DeleteMyAccountUseCase;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import me.jinjjahalgae.domain.user.User;

import java.time.LocalDateTime;

/**
 * 유저 탈퇴 (soft delete)
 */
@RequiredArgsConstructor
@Service
public class DeleteMyAccountUseCaseImpl implements DeleteMyAccountUseCase {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void execute(Long userId) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> ErrorCode.USER_NOT_FOUND.domainException("유저를 찾을 수 없음"));
                
        user.delete(LocalDateTime.now());
    }
} 