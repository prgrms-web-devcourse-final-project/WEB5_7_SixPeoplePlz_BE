package me.jinjjahalgae.domain.user.usecase.get.myinfo;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.user.User;
import me.jinjjahalgae.domain.user.UserRepository;
import me.jinjjahalgae.domain.user.mapper.UserMapper;
import me.jinjjahalgae.domain.user.usecase.common.dto.MyInfoResponse;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.stereotype.Service;

/**
 * 유저 정보 조회
 */
@RequiredArgsConstructor
@Service
public class GetMyInfoUseCaseImpl implements GetMyInfoUseCase {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public MyInfoResponse execute(Long userId) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> ErrorCode.USER_NOT_FOUND.domainException("유저를 찾을 수 없음"));
            
        return userMapper.toMyInfoResponse(user);
    }
} 