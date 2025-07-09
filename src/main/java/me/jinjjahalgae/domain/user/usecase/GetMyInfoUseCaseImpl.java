package me.jinjjahalgae.domain.user.usecase;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.user.User;
import me.jinjjahalgae.domain.user.UserRepository;
import me.jinjjahalgae.domain.user.dto.MyInfoResponse;
import me.jinjjahalgae.domain.user.usecase.interfaces.GetMyInfoUseCase;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.stereotype.Service;

/**
 * 유저 정보 조회
 */
@RequiredArgsConstructor
@Service
public class GetMyInfoUseCaseImpl implements GetMyInfoUseCase {
    private final UserRepository userRepository;

    @Override
    public MyInfoResponse execute(Long userId) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> ErrorCode.USER_NOT_FOUND.domainException("유저를 찾을 수 없음"));
            
        return new MyInfoResponse(
            user.getId(),
            user.getName(),
            user.getNickname(),
            user.getEmail()
        );
    }
} 