package me.jinjjahalgae.domain.user.usecase.update_my_info;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.user.User;
import me.jinjjahalgae.domain.user.UserRepository;
import me.jinjjahalgae.domain.user.mapper.UserMapper;
import me.jinjjahalgae.domain.user.usecase.common.dto.MyInfoResponse;
import me.jinjjahalgae.domain.user.usecase.update_my_info.dto.UpdateMyInfoRequest;
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
    private final UserMapper userMapper;

    @Override
    @Transactional
    public MyInfoResponse execute(Long userId, UpdateMyInfoRequest request) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> ErrorCode.USER_NOT_FOUND.domainException("유저를 찾을 수 없음"));

        user.updateNickname(request.nickname());

        return userMapper.toMyInfoResponse(user);
    }
} 