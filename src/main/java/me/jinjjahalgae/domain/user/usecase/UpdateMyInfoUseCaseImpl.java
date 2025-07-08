package me.jinjjahalgae.domain.user.usecase;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.user.User;
import me.jinjjahalgae.domain.user.UserRepository;
import me.jinjjahalgae.domain.user.dto.MyInfoResponse;
import me.jinjjahalgae.domain.user.dto.UpdateMyInfoRequest;
import me.jinjjahalgae.domain.user.usecase.interfaces.UpdateMyInfoUseCase;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 유저 정보 수정
 */
@RequiredArgsConstructor
@Service
public class UpdateMyInfoUseCaseImpl implements UpdateMyInfoUseCase {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public MyInfoResponse execute(Long userId, UpdateMyInfoRequest request) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> ErrorCode.USER_NOT_FOUND.domainException("유저를 찾을 수 없음"));

        user.updateNickname(request.nickname());

        return new MyInfoResponse(
            user.getId(),
            user.getName(),
            user.getNickname(),
            user.getEmail()
        );
    }
} 